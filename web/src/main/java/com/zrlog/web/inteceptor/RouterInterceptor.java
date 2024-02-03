package com.zrlog.web.inteceptor;

import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.hibegin.http.server.api.Interceptor;
import com.zrlog.admin.web.interceptor.AdminInterceptor;
import com.zrlog.blog.web.interceptor.BlogArticleInterceptor;

/**
 * 没有对应URI path的过滤的配置，于是需要手动通过URI path进行划分到不同的Interception。
 */
public class RouterInterceptor implements Interceptor {

    private final AdminInterceptor adminInterceptor = new AdminInterceptor();

    private final PluginInterceptor pluginInterceptor = new PluginInterceptor();

    private final BlogArticleInterceptor blogArticleInterceptor = new BlogArticleInterceptor();

    @Override
    public boolean doInterceptor(HttpRequest request, HttpResponse response) throws Exception {
        String actionKey = request.getUri();
        if (PluginInterceptor.pluginHandlerPaths.stream().anyMatch(actionKey::startsWith)) {
            pluginInterceptor.doInterceptor(request, response);
            return false;
        }
        //这样写一点页不优雅，路径少还好，多了就痛苦了
        if (actionKey.startsWith("/admin") || actionKey.startsWith("/api/admin")) {
            adminInterceptor.doInterceptor(request, response);
        } else {
            blogArticleInterceptor.doInterceptor(request, response);
        }
        return true;
    }
}
