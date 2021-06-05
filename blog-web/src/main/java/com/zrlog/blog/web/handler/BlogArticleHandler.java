package com.zrlog.blog.web.handler;

import com.hibegin.common.util.IOUtil;
import com.jfinal.handler.Handler;
import com.zrlog.admin.web.token.AdminTokenService;
import com.zrlog.blog.web.plugin.RequestInfo;
import com.zrlog.blog.web.plugin.RequestStatisticsPlugin;
import com.zrlog.blog.web.util.WebTools;
import com.zrlog.business.cache.CacheService;
import com.zrlog.business.util.InstallUtils;
import com.zrlog.common.Constants;
import com.zrlog.common.vo.AdminTokenVO;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.ZrLogUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 静态化文章页，加快文章页的响应，压缩html文本，提供自定义插件标签的解析，静态资源文件的浏览器缓存问题
 */
public class BlogArticleHandler extends Handler {

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
        if (target.startsWith("/admin/") || target.startsWith("/api/") || target.startsWith("/install/")) {
            this.next.handle(target, request, response, isHandled);
            return;
        }
        try {
            if (catGeneratorHtml(target)) {
                //首页静态化
                if (target.equals("/")) {
                    responseHtmlFile(target, request, response, isHandled, INDEX_PAGE_HTML);
                } else {
                    String fileName = request.getServletPath();
                    responseHtmlFile(target.substring(0, target.indexOf(".")), request, response, isHandled, fileName);
                }
            } else {
                this.next.handle(target, request, getWrapper(request, response), isHandled);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            //仅保留非静态资源请求或者是以 .html结尾的
            if (InstallUtils.isInstalled() && !target.contains(".") || target.endsWith(".html")) {
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
            File htmlFile = new CacheService().loadHtmlFile(I18nUtil.getAcceptLocal(request) + fileName);
            response.setContentType("text/html;charset=UTF-8");
            if (htmlFile.exists() && !ZrLogUtil.isStaticBlogPlugin(request)) {
                isHandled[0] = true;
                response.getOutputStream().write(IOUtil.getByteByInputStream(new FileInputStream(htmlFile)));
            } else {
                MyHttpServletResponseWrapper responseWrapper = getWrapper(request, response);
                this.next.handle(target, request, responseWrapper, isHandled);
                new CacheService().saveResponseBodyToHtml(htmlFile, responseWrapper.getWriter().getResponseBody());
            }
        } finally {
            response.getOutputStream().flush();
            response.getOutputStream().close();
        }
    }

}
