package com.zrlog.web.handler;

import com.hibegin.common.util.FileUtils;
import com.hibegin.common.util.IOUtil;
import com.jfinal.core.JFinal;
import com.jfinal.handler.Handler;
import com.jfinal.kit.PathKit;
import com.zrlog.common.Constants;
import com.zrlog.service.AdminTokenThreadLocal;
import com.zrlog.util.ZrLogUtil;
import com.zrlog.web.util.WebTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

/**
 * 用于对静态文件的请求的检查，和静态化文章页，加快文章页的响应，压缩html文本，提供自定义插件标签的解析，静态资源文件的浏览器缓存问题
 */
public class GlobalResourceHandler extends Handler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalResourceHandler.class);
    private static final String PAGE_END_TAG = "<none id='SP_" + System.currentTimeMillis() + "'></none>";

    //不希望部分技术人走后门，拦截一些不合法的请求
    private static final Set<String> FORBIDDEN_URI_EXT_SET = new HashSet<>();

    static {
        //由于程序的.jsp文件没有存放在WEB-INF目录，为了防止访问.jsp页面获得的没有数据的页面，或则是错误的页面。
        FORBIDDEN_URI_EXT_SET.add(".jsp");
        //这主要用在主题目录下面的配置文件。
        FORBIDDEN_URI_EXT_SET.add(".properties");
    }

    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
        String url = WebTools.getRealScheme(request) + "://" + request.getHeader("host") + request.getContextPath() + "/";
        request.setAttribute("basePath", url);
        request.setAttribute("pageEndTag", PAGE_END_TAG);
        String ext = null;
        if (target.contains("/")) {
            String name = target.substring(target.lastIndexOf('/'));
            if (name.contains(".")) {
                ext = name.substring(name.lastIndexOf('.'));
            }
        }
        try {
            final ResponseRenderPrintWriter responseRenderPrintWriter = new ResponseRenderPrintWriter(response.getOutputStream(), !JFinal.me().getConstants().getDevMode(), url, PAGE_END_TAG, request);
            response = new HttpServletResponseWrapper(response) {
                @Override
                public PrintWriter getWriter() throws IOException {
                    return responseRenderPrintWriter;
                }
            };
            if (ext != null) {
                if (!FORBIDDEN_URI_EXT_SET.contains(ext)) {
                    // 处理静态化文件,仅仅缓存文章页(变化较小)
                    if (target.endsWith(".html") && target.startsWith("/post/")) {
                        target = target.substring(0, target.lastIndexOf("."));
                        if (Constants.isStaticHtmlStatus()) {
                            String path = new String(request.getServletPath().getBytes("ISO-8859-1"), "UTF-8");
                            File htmlFile = new File(PathKit.getWebRootPath() + path);
                            response.setContentType("text/html;charset=UTF-8");
                            if (htmlFile.exists() && !ZrLogUtil.isStaticBlogPlugin(request)) {
                                isHandled[0] = true;
                                response.getOutputStream().write(IOUtil.getByteByInputStream(new FileInputStream(htmlFile)));
                            } else {
                                this.next.handle(target, request, response, isHandled);
                                saveResponseBodyToHtml(htmlFile, responseRenderPrintWriter.getResponseBody());
                            }
                        } else {
                            this.next.handle(target, request, response, isHandled);
                        }
                    } else {
                        this.next.handle(target, request, response, isHandled);
                    }
                } else {
                    try {
                        //非法请求, 返回403
                        response.sendError(403);
                    } catch (IOException e) {
                        LOGGER.error("", e);
                    }
                }
            } else {
                this.next.handle(target, request, response, isHandled);
            }
        } catch (Exception e) {
            LOGGER.error("", e);
        } finally {
            AdminTokenThreadLocal.remove();
        }
    }

    /**
     * 将一个网页转化对应文件，用于静态化文章页
     */

    private void saveResponseBodyToHtml(File file, String copy) {
        try {
            byte[] bytes = copy.getBytes("UTF-8");
            FileUtils.tryResizeDiskSpace(PathKit.getWebRootPath() + "/post", bytes.length, Constants.getMaxCacheHtmlSize());
            if (!file.exists()) {
                file.getParentFile().mkdirs();
            }
            IOUtil.writeBytesToFile(bytes, file);
        } catch (IOException e) {
            LOGGER.error("saveResponseBodyToHtml error", e);
        }
    }

}
