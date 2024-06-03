package com.zrlog.web.inteceptor;

import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.hibegin.http.server.api.Interceptor;
import com.hibegin.http.server.util.PathUtil;
import com.hibegin.http.server.web.MethodInterceptor;
import com.zrlog.blog.web.interceptor.BlogArticleInterceptor;
import com.zrlog.blog.web.plugin.RequestInfo;
import com.zrlog.blog.web.plugin.RequestStatisticsPlugin;
import com.zrlog.blog.web.util.WebTools;
import com.zrlog.business.util.InstallUtils;
import com.zrlog.common.Constants;
import com.zrlog.util.ZrLogUtil;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class StaticResourceInterceptor implements Interceptor {

    public static final List<String> staticResourcePath = Arrays.asList("/assets", "/admin/static", "/admin/vendors", "/install/static");

    public StaticResourceInterceptor() {
    }

    @Override
    public boolean doInterceptor(HttpRequest request, HttpResponse response) throws Exception {
        String actionKey = request.getUri();
        //打包过后的静态资源文件进行拦截
        if (staticResourcePath.stream().anyMatch(e -> request.getUri().startsWith(e + "/"))) {
            response.addHeader("Cache-Control", "max-age=31536000, immutable"); // 1 年的秒数
            new MethodInterceptor().doInterceptor(request, response);
            return false;
        }
        try {
            File staticFile = PathUtil.getStaticFile(actionKey);
            //静态文件进行拦截
            if (staticFile.isFile() && staticFile.exists()) {
                //缓存静态资源文件
                if (Constants.zrLogConfig.getCacheService().isCacheableByRequest(request)) {
                    response.addHeader("Cache-Control", "max-age=31536000, immutable"); // 1 年的秒数
                }
                response.writeFile(staticFile);
                return false;
            }
            if (BlogArticleInterceptor.catGeneratorHtml(actionKey)) {
                File cacheFile = Constants.zrLogConfig.getCacheService().loadCacheFile(request);
                if (cacheFile.exists() && !ZrLogUtil.isStaticBlogPlugin(request)) {
                    response.writeFile(cacheFile);
                    return false;
                }
            }
        } finally {
            //仅保留非静态资源请求或者是以 .html结尾的
            if (InstallUtils.isInstalled() && !actionKey.contains(".") || actionKey.endsWith(".html")) {
                RequestInfo requestInfo = new RequestInfo();
                requestInfo.setIp(WebTools.getRealIp(request));
                requestInfo.setUrl(WebTools.getHomeUrl(request));
                requestInfo.setUserAgent(request.getHeader("User-Agent"));
                requestInfo.setRequestTime(System.currentTimeMillis());
                requestInfo.setRequestUri(actionKey);
                RequestStatisticsPlugin.record(requestInfo);
            }
        }
        return true;
    }
}
