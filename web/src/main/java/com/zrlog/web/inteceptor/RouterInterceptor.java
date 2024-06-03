package com.zrlog.web.inteceptor;

import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.hibegin.http.server.api.Interceptor;
import com.zrlog.admin.web.interceptor.AdminInterceptor;
import com.zrlog.admin.web.interceptor.AdminPwaInterceptor;
import com.zrlog.blog.web.interceptor.BlogArticleInterceptor;
import com.zrlog.blog.web.interceptor.PwaInterceptor;

/**
 * 没有对应URI path的过滤的配置，于是需要手动通过URI path进行划分到不同的Interception。
 */
public class RouterInterceptor implements Interceptor {

    private final AdminInterceptor adminInterceptor = new AdminInterceptor();
    private final AdminPwaInterceptor adminPwaInterceptor = new AdminPwaInterceptor();
    private final PwaInterceptor pwaInterceptor = new PwaInterceptor();

    private final PluginInterceptor pluginInterceptor = new PluginInterceptor();

    private final BlogArticleInterceptor blogArticleInterceptor = new BlogArticleInterceptor();

    public RouterInterceptor() {
    }

    @Override
    public boolean doInterceptor(HttpRequest request, HttpResponse response) throws Exception {
        if (pluginInterceptor.isHandleAble(request)) {
            pluginInterceptor.doInterceptor(request, response);
            return false;
        } else if (adminPwaInterceptor.isHandleAble(request)) {
            adminPwaInterceptor.doInterceptor(request, response);
            return false;
        } else if (adminInterceptor.isHandleAble(request)) {
            adminInterceptor.doInterceptor(request, response);
        } else if (pwaInterceptor.isHandleAble(request)) {
            pwaInterceptor.doInterceptor(request, response);
        } else {
            blogArticleInterceptor.doInterceptor(request, response);
        }
        return true;
    }
}
