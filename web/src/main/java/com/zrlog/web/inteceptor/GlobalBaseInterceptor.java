package com.zrlog.web.inteceptor;

import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.hibegin.http.server.api.Interceptor;
import com.zrlog.blog.web.util.WebTools;
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
    private final Set<String> forbiddenUriSet = new HashSet<>();

    public GlobalBaseInterceptor() {
        //不希望部分技术人走后门，拦截一些不合法的请求
        //由于程序的.flt文件没有存放在conf目录，为了防止访问.ftl页面获得的没有数据的页面，或则是错误的页面。
        forbiddenUriExtSet.add(".ftl");
        //这主要用在主题目录下面的配置文件。
        forbiddenUriExtSet.add(".properties");
        if (ZrLogUtil.isWarMode()) {
            forbiddenUriSet.add(".jsp");
        }
        //静态文件
        forbiddenUriSet.add(Constants.INSTALL_HTML_PAGE);
        forbiddenUriSet.add(Constants.ADMIN_HTML_PAGE);
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
        if (forbiddenUriExtSet.stream().anyMatch(target::endsWith) || forbiddenUriSet.contains(target)) {
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
