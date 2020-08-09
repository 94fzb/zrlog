package com.zrlog.web.handler;

import com.hibegin.common.util.FileUtils;
import com.hibegin.common.util.IOUtil;
import com.jfinal.core.JFinal;
import com.jfinal.handler.Handler;
import com.jfinal.kit.PathKit;
import com.zrlog.common.Constants;
import com.zrlog.common.vo.AdminTokenVO;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.ZrLogUtil;
import com.zrlog.web.config.ZrLogConfig;
import com.zrlog.web.plugin.RequestInfo;
import com.zrlog.web.plugin.RequestStatisticsPlugin;
import com.zrlog.web.token.AdminTokenService;
import com.zrlog.web.util.WebTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

/**
 * 用于对静态文件的请求的检查，和静态化文章页，加快文章页的响应，压缩html文本，提供自定义插件标签的解析，静态资源文件的浏览器缓存问题
 */
public class GlobalResourceHandler extends Handler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalResourceHandler.class);
    private static final String PAGE_END_TAG = "<none id='SP_" + System.currentTimeMillis() + "'></none>";
    public static final String CACHE_HTML_PATH = PathKit.getWebRootPath() + "/_cache/";
    private static final ThreadLocal<Long> REQUEST_START_TIME = new ThreadLocal<>();
    private static final String INDEX_PAGE_HTML = "/index.html";

    /**
     * `
     * 不希望部分技术人走后门，拦截一些不合法的请求
     */
    private static final Set<String> FORBIDDEN_URI_EXT_SET = new HashSet<>();

    static {
        //由于程序的.jsp文件没有存放在WEB-INF目录，为了防止访问.jsp页面获得的没有数据的页面，或则是错误的页面。
        FORBIDDEN_URI_EXT_SET.add(".jsp");
        FORBIDDEN_URI_EXT_SET.add(".ftl");
        //这主要用在主题目录下面的配置文件。
        FORBIDDEN_URI_EXT_SET.add(".properties");
    }

    /**
     * 处理静态化文件,仅仅缓存文章页(变化较小)
     */
    private static boolean catGeneratorHtml(String targetUri) {
        return "/".equals(targetUri) || (targetUri.startsWith("/" + Constants.getArticleUri()) && targetUri.endsWith(".html"));
    }

    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
        long start = System.currentTimeMillis();
        REQUEST_START_TIME.set(start);
        String url = WebTools.getHomeUrl(request);
        request.setAttribute("basePath", url);
        request.setAttribute("baseWithHostPath", WebTools.getHomeUrlWithHostNotProtocol(request));
        request.setAttribute("pageEndTag", PAGE_END_TAG);
        String ext = null;
        if (target.contains("/")) {
            String name = target.substring(target.lastIndexOf('/'));
            if (name.contains(".")) {
                ext = name.substring(name.lastIndexOf('.'));
            }
        }
        try {
            AdminTokenVO adminTokenVO = new AdminTokenService().getAdminTokenVO(request);
            final ResponseRenderPrintWriter responseRenderPrintWriter = new ResponseRenderPrintWriter(response.getOutputStream(), url, PAGE_END_TAG, request, response, adminTokenVO);
            response = new MyHttpServletResponseWrapper(response, responseRenderPrintWriter);
            if (ext != null) {
                if (!FORBIDDEN_URI_EXT_SET.contains(ext)) {
                    if (catGeneratorHtml(target)) {
                        target = target.substring(0, target.lastIndexOf("."));
                        if (Constants.isStaticHtmlStatus()) {
                            String path = new String(request.getServletPath().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                            if ("/".equals(path)) {
                                path = INDEX_PAGE_HTML;
                            }
                            responseHtmlFile(target, request, response, isHandled, responseRenderPrintWriter, new File(CACHE_HTML_PATH + I18nUtil.getAcceptLanguage(request) + path));
                        } else {
                            this.next.handle(target, request, response, isHandled);
                        }
                    } else {
                        this.next.handle(target, request, response, isHandled);
                    }
                } else {
                    //非法请求, 返回403
                    response.sendError(403);
                }
            } else {
                //首页静态化
                if ("/".equals(target) && Constants.isStaticHtmlStatus()) {
                    responseHtmlFile(target, request, response, isHandled, responseRenderPrintWriter, new File(CACHE_HTML_PATH + I18nUtil.getAcceptLanguage(request) + INDEX_PAGE_HTML));
                } else {
                    this.next.handle(target, request, response, isHandled);
                    //JFinal， JsonRender 移除了 flush()，需要手动 flush，针对JFinal3.3 以后版本
                    response.getWriter().flush();
                }
            }
        } catch (Exception e) {
            LOGGER.error("", e);
        } finally {
            I18nUtil.removeI18n();
            //开发环境下面打印整个请求的耗时，便于优化代码
            if (BlogBuildInfoUtil.isDev()) {
                LOGGER.info("{} used time {}", request.getServletPath(), System.currentTimeMillis() - start);
            }
            //仅保留非静态资源请求或者是以 .html结尾的
            if (ZrLogConfig.isInstalled() && !target.contains(".") || target.endsWith(".html")) {
                RequestInfo requestInfo = new RequestInfo();
                requestInfo.setIp(WebTools.getRealIp(request));
                requestInfo.setUrl(url);
                requestInfo.setUserAgent(request.getHeader("User-Agent"));
                requestInfo.setRequestTime(System.currentTimeMillis());
                requestInfo.setRequestUri(target);
                RequestStatisticsPlugin.record(requestInfo);
            }
        }
        REQUEST_START_TIME.remove();
    }

    private void responseHtmlFile(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled, ResponseRenderPrintWriter responseRenderPrintWriter, File htmlFile) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        if (htmlFile.exists() && !ZrLogUtil.isStaticBlogPlugin(request)) {
            isHandled[0] = true;
            response.getOutputStream().write(IOUtil.getByteByInputStream(new FileInputStream(htmlFile)));
        } else {
            this.next.handle(target, request, response, isHandled);
            saveResponseBodyToHtml(htmlFile, responseRenderPrintWriter.getResponseBody());
        }
    }

    /**
     * 将一个网页转化对应文件，用于静态化文章页
     */
    private void saveResponseBodyToHtml(File file, String copy) {
        if (copy != null) {
            byte[] bytes = copy.getBytes(StandardCharsets.UTF_8);
            FileUtils.tryResizeDiskSpace(CACHE_HTML_PATH + Constants.getArticleUri(), bytes.length, Constants.getMaxCacheHtmlSize());
            if (!file.exists()) {
                file.getParentFile().mkdirs();
            }
            IOUtil.writeBytesToFile(bytes, file);
        }
    }

    public static void printUserTime(String key) {
        if (JFinal.me().getConstants().getDevMode()) {
            LOGGER.info("key = " + key + " usedTime " + (System.currentTimeMillis() - REQUEST_START_TIME.get()));
        }
    }

}
