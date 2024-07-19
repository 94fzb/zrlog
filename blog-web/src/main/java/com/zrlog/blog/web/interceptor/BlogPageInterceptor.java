package com.zrlog.blog.web.interceptor;

import com.hibegin.common.util.FileUtils;
import com.hibegin.http.server.api.HandleAbleInterceptor;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.hibegin.http.server.util.FreeMarkerUtil;
import com.hibegin.http.server.util.MimeTypeUtil;
import com.hibegin.http.server.web.Controller;
import com.zrlog.business.plugin.StaticSitePlugin;
import com.zrlog.business.service.TemplateHelper;
import com.zrlog.business.util.InstallUtils;
import com.zrlog.common.Constants;
import com.zrlog.util.ZrLogUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 静态化文章页，加快文章页的响应，压缩html文本，提供自定义插件标签的解析，静态资源文件的浏览器缓存问题
 */
public class BlogPageInterceptor implements HandleAbleInterceptor {


    /**
     * 处理静态化文件,仅仅缓存文章页(变化较小)
     */
    public static boolean catGeneratorHtml(String targetUri) {
        if (!Constants.isStaticHtmlStatus()) {
            return false;
        }
        return "/".equals(targetUri) || (targetUri.startsWith("/" + Constants.getArticleUri()) && targetUri.endsWith(".html"));
    }

    @Override
    public boolean doInterceptor(HttpRequest request, HttpResponse response) throws Exception {
        if (request.getUri().startsWith(Constants.DEFAULT_TEMPLATE_PATH)) {
            try (InputStream resourceAsStream = BlogPageInterceptor.class.getResourceAsStream(request.getUri())) {
                if (Objects.nonNull(resourceAsStream)) {
                    response.getHeader().put("Content-Type", MimeTypeUtil.getMimeStrByExt(FileUtils.getFileExt(request.getUri())));
                    response.write(resourceAsStream, 200);
                    return false;
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        String target = request.getUri();
        if (InstallUtils.isInstalled()) {
            Constants.zrLogConfig.getCacheService().refreshInitDataCacheAsync(request, false).join();
        } else {
            response.redirect("/install?ref=" + request.getUri());
            return false;
        }
        Method method = request.getServerConfig().getRouter().getRouterMap().get(target);
        if (Objects.isNull(method) && target.endsWith(".html")) {
            method = request.getServerConfig().getRouter().getRouterMap().get(target.substring(0, target.length() - 5));
        }
        if (Objects.isNull(method)) {
            method = request.getServerConfig().getRouter().getRouterMap().get("/detail");
        }
        if (target.startsWith("/all-")) {
            method = request.getServerConfig().getRouter().getRouterMap().get("/index");
        }
        if (target.startsWith("/sort/")) {
            method = request.getServerConfig().getRouter().getRouterMap().get("/sort");
        }
        if (target.startsWith("/search/")) {
            method = request.getServerConfig().getRouter().getRouterMap().get("/search");
        }
        if (target.startsWith("/tag/")) {
            method = request.getServerConfig().getRouter().getRouterMap().get("/tag");
        }
        if (target.startsWith("/record/")) {
            method = request.getServerConfig().getRouter().getRouterMap().get("/record");
        }
        if (Objects.isNull(method)) {
            return true;
        }
        Object invoke = method.invoke(Controller.buildController(method, request, response));
        if (Objects.nonNull(invoke)) {
            TemplateHelper.fullTemplateInfo(request);
            TemplateUtils.initTemplate();
            if(TemplateHelper.isArrangeable(request) && TemplateUtils.existsByTemplateName("arrange")){
                invoke = "arrange";
            }
            String htmlStr = FreeMarkerUtil.renderToFM(invoke.toString(), request);
            render(htmlStr, target, request, response);
        }
        return false;
    }


    private static void render(String htmlStr, String target, HttpRequest request, HttpResponse response) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ResponseRenderPrintWriter responseRenderPrintWriter = new ResponseRenderPrintWriter(byteArrayOutputStream, "/", request, response, null)) {
            responseRenderPrintWriter.write(htmlStr);
            String realHtmlStr = responseRenderPrintWriter.getResponseBody();
            if (!ZrLogUtil.isStaticBlogPlugin(request)) {
                response.renderHtmlStr(realHtmlStr);
            }
            if (BlogPageInterceptor.catGeneratorHtml(target)) {
                request.getAttr().put(StaticSitePlugin.HTML_FILE_KEY, Constants.zrLogConfig.getCacheService().saveResponseBodyToHtml(request, realHtmlStr));
            }
        }
    }

    @Override
    public boolean isHandleAble(HttpRequest request) {
        return true;
    }
}
