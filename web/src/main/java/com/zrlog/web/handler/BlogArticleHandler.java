package com.zrlog.web.handler;

import com.hibegin.common.util.FileUtils;
import com.hibegin.common.util.IOUtil;
import com.jfinal.handler.Handler;
import com.jfinal.kit.PathKit;
import com.zrlog.common.Constants;
import com.zrlog.common.vo.AdminTokenVO;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.ZrLogUtil;
import com.zrlog.web.config.ZrLogConfig;
import com.zrlog.web.plugin.RequestInfo;
import com.zrlog.web.plugin.RequestStatisticsPlugin;
import com.zrlog.web.token.AdminTokenService;
import com.zrlog.web.util.WebTools;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 静态化文章页，加快文章页的响应，压缩html文本，提供自定义插件标签的解析，静态资源文件的浏览器缓存问题
 */
public class BlogArticleHandler extends Handler {

    public static final String CACHE_HTML_PATH = PathKit.getWebRootPath() + "/_cache/";

    private static final String INDEX_PAGE_HTML = "/index.html";

    /**
     * 处理静态化文件,仅仅缓存文章页(变化较小)
     */
    private static boolean catGeneratorHtml(String targetUri) {
        if (!Constants.isStaticHtmlStatus()) {
            return false;
        }
        return "/".equals(targetUri) || (targetUri.startsWith("/" + Constants.getArticleUri()) && targetUri.endsWith(".html"));
    }

    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
        if (target.startsWith("/admin") || target.startsWith("/api/") || target.startsWith("/install")) {
            this.next.handle(target, request, response, isHandled);
            return;
        }
        try {
            if (catGeneratorHtml(target)) {
                //首页静态化
                if (target.equals("/")) {
                    responseHtmlFile(target, request, response, isHandled, INDEX_PAGE_HTML);
                } else {
                    String fileName = new String(request.getServletPath().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                    responseHtmlFile(target.substring(0, target.indexOf(".")), request, response, isHandled, fileName);
                }
            } else {
                this.next.handle(target, request, getWrapper(request, response), isHandled);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            //仅保留非静态资源请求或者是以 .html结尾的
            if (ZrLogConfig.isInstalled() && !target.contains(".") || target.endsWith(".html")) {
                RequestInfo requestInfo = new RequestInfo();
                requestInfo.setIp(WebTools.getRealIp(request));
                requestInfo.setUrl(WebTools.getHomeUrl(request));
                requestInfo.setUserAgent(request.getHeader("User-Agent"));
                requestInfo.setRequestTime(System.currentTimeMillis());
                requestInfo.setRequestUri(target);
                RequestStatisticsPlugin.record(requestInfo);
            }
        }
    }

    private MyHttpServletResponseWrapper getWrapper(HttpServletRequest request, HttpServletResponse response) throws IOException {
        AdminTokenVO adminTokenVO = new AdminTokenService().getAdminTokenVO(request);
        final ResponseRenderPrintWriter responseRenderPrintWriter = new ResponseRenderPrintWriter(response.getOutputStream(), WebTools.getHomeUrl(request), request, response, adminTokenVO);
        return new MyHttpServletResponseWrapper(response, responseRenderPrintWriter);
    }

    private void responseHtmlFile(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled, String fileName) throws IOException {
        try {
            File htmlFile = new File(CACHE_HTML_PATH + I18nUtil.getAcceptLanguage(request) + fileName);
            response.setContentType("text/html;charset=UTF-8");
            if (htmlFile.exists() && !ZrLogUtil.isStaticBlogPlugin(request)) {
                isHandled[0] = true;
                response.getOutputStream().write(IOUtil.getByteByInputStream(new FileInputStream(htmlFile)));
            } else {
                MyHttpServletResponseWrapper responseWrapper = getWrapper(request, response);
                this.next.handle(target, request, responseWrapper, isHandled);
                saveResponseBodyToHtml(htmlFile, responseWrapper.getWriter().getResponseBody());
            }
        } finally {
            response.getOutputStream().flush();
            response.getOutputStream().close();
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
}
