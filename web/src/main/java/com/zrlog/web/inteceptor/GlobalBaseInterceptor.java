package com.zrlog.web.inteceptor;

import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.hibegin.http.server.api.Interceptor;
import com.zrlog.blog.web.util.WebTools;
import com.zrlog.business.plugin.StaticSitePlugin;
import com.zrlog.common.Constants;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.ZrLogUtil;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 用于对静态文件的请求的检查
 */
public class GlobalBaseInterceptor implements Interceptor {


    private final Set<String> forbiddenUriExtSet = new HashSet<>();

    public GlobalBaseInterceptor() {
        //由于程序的.flt文件没有存放在conf目录，为了防止访问.ftl页面获得的没有数据的页面，或则是错误的页面。
        forbiddenUriExtSet.add(".ftl");
        //这主要用在主题目录下面的配置文件。
        forbiddenUriExtSet.add(".properties");
        if (ZrLogUtil.isWarMode()) {
            forbiddenUriExtSet.add(".jsp");
        }
    }

    /**
     * 不希望部分技术人走后门，拦截一些不合法的请求
     * @param request
     * @return
     */
    private boolean isForbiddenUri(HttpRequest request) {
        String target = request.getUri();
        if (forbiddenUriExtSet.stream().anyMatch(target::endsWith)) {
            //非法请求, 返回403
            return true;
        }
        if (target.startsWith(Constants.ADMIN_URI_BASE_PATH) && target.endsWith(".html")) {
            //非法请求, 返回403
            return !StaticSitePlugin.isStaticPluginRequest(request);
        }
        return false;
    }

    @Override
    public boolean doInterceptor(HttpRequest request, HttpResponse response) {
        String target = request.getUri();
        request.getAttr().put("requrl", ZrLogUtil.getFullUrl(request));
        request.getAttr().put("reqUriPath", Objects.requireNonNullElse(request.getUri(), "/"));
        request.getAttr().put("reqQueryString", Objects.requireNonNullElse(request.getQueryStr(), ""));
        Constants.setLastAccessTime(System.currentTimeMillis());
        //便于Wappalyzer读取
        response.addHeader("X-ZrLog", BlogBuildInfoUtil.getVersion());
        if (isForbiddenUri(request)) {
            //非法请求, 返回403
            response.renderCode(403);
            return false;
        }
        if (target.startsWith("/api")) {
            response.addHeader("Content-Type", "application/json");
        }
        request.getAttr().put("basePath", WebTools.getHomeUrl(request));
        request.getAttr().put("baseWithHostPath", ZrLogUtil.getHomeUrlWithHostNotProtocol(request));
        return true;
    }

}
