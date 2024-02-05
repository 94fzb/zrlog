package com.zrlog.blog.web.interceptor;

import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.hibegin.http.server.api.Interceptor;
import com.hibegin.http.server.util.FreeMarkerUtil;
import com.hibegin.http.server.util.PathUtil;
import com.hibegin.http.server.web.Controller;
import com.hibegin.http.server.web.MethodInterceptor;
import com.zrlog.blog.web.plugin.RequestInfo;
import com.zrlog.blog.web.plugin.RequestStatisticsPlugin;
import com.zrlog.blog.web.util.WebTools;
import com.zrlog.business.cache.CacheService;
import com.zrlog.business.exception.InstalledException;
import com.zrlog.business.service.TemplateHelper;
import com.zrlog.business.util.InstallUtils;
import com.zrlog.common.Constants;
import com.zrlog.model.WebSite;
import com.zrlog.util.ZrLogUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

/**
 * 静态化文章页，加快文章页的响应，压缩html文本，提供自定义插件标签的解析，静态资源文件的浏览器缓存问题
 */
public class BlogArticleInterceptor implements Interceptor {


    private final CacheService cacheService = new CacheService();

    /**
     * 处理静态化文件,仅仅缓存文章页(变化较小)
     */
    public static boolean catGeneratorHtml(String targetUri) {
        if (!Constants.isStaticHtmlStatus()) {
            return false;
        }
        return "/".equals(targetUri) || (targetUri.startsWith("/" + Constants.getArticleUri()) && targetUri.endsWith(".html"));
    }


    private void initTemplate(){
        if (!InstallUtils.isInstalled()) {
            return;
        }
        String configTemplate = Constants.WEB_SITE.getOrDefault("template",Constants.DEFAULT_TEMPLATE_PATH).toString();
        File path = new File(PathUtil.getStaticPath() + configTemplate);
        if (!path.exists()) {
            path = new File(PathUtil.getStaticPath() + Constants.DEFAULT_TEMPLATE_PATH);
        }
        try {
            FreeMarkerUtil.init(path.getPath());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean doInterceptor(HttpRequest request, HttpResponse response) throws Exception {
        String target = request.getUri();
        if (target.startsWith("/api/install") && InstallUtils.isInstalled()) {
            throw new InstalledException();
        }
        if (target.startsWith("/api")) {
            new MethodInterceptor().doInterceptor(request, response);
            return true;
        }
        cacheService.refreshInitDataCache(request, false);
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
        Object invoke = method.invoke(ZrLogUtil.buildController(method, request, response));
        if (Objects.nonNull(invoke)) {
            TemplateHelper.fullTemplateInfo(request);
            initTemplate();
            String htmlStr = FreeMarkerUtil.renderToFM(invoke.toString(), request);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try (ResponseRenderPrintWriter responseRenderPrintWriter = new ResponseRenderPrintWriter(byteArrayOutputStream, "/", request, response, null)) {
                responseRenderPrintWriter.write(htmlStr);
                String realHtmlStr = responseRenderPrintWriter.getResponseBody();
                response.renderHtmlStr(realHtmlStr);

                if (catGeneratorHtml(target)) {
                    cacheService.saveResponseBodyToHtml(request, realHtmlStr);
                }
            }
        }
        return true;
    }

}
