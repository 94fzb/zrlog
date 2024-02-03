package com.zrlog.web.inteceptor;

import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.hibegin.http.server.api.Interceptor;
import com.zrlog.blog.web.util.WebTools;
import com.zrlog.common.Constants;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.ZrLogUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * 用于对静态文件的请求的检查
 */
public class GlobalBaseInterceptor implements Interceptor {

    /**
     * 不希望部分技术人走后门，拦截一些不合法的请求
     */
    private static final Set<String> FORBIDDEN_URI_EXT_SET = new HashSet<>();
    private static final Set<String> FORBIDDEN_URI_SET = new HashSet<>();

    static {
        //由于程序的.flt文件没有存放在conf目录，为了防止访问.ftl页面获得的没有数据的页面，或则是错误的页面。
        FORBIDDEN_URI_EXT_SET.add(".ftl");
        //这主要用在主题目录下面的配置文件。
        FORBIDDEN_URI_EXT_SET.add(".properties");
        //静态文件
        FORBIDDEN_URI_SET.add(Constants.INSTALL_HTML_PAGE);
        FORBIDDEN_URI_SET.add(Constants.ADMIN_HTML_PAGE);
    }

    @Override
    public boolean doInterceptor(HttpRequest request, HttpResponse response) {
        String target = request.getUri();
        request.getAttr().put("requrl", ZrLogUtil.getFullUrl(request));
        Constants.setLastAccessTime(System.currentTimeMillis());
        //便于Wappalyzer读取
        response.addHeader("X-ZrLog", BlogBuildInfoUtil.getVersion());
        if (FORBIDDEN_URI_EXT_SET.stream().anyMatch(target::endsWith) || FORBIDDEN_URI_SET.contains(target)) {
            //非法请求, 返回403
            response.renderCode(403);
            return false;
        }
        if (target.startsWith("/api")) {
            response.addHeader("Content-Type", "application/json;charset=UTF-8");
        }
        request.getAttr().put("basePath", WebTools.getHomeUrl(request));
        request.getAttr().put("baseWithHostPath", WebTools.getHomeUrlWithHostNotProtocol(request));
        return true;
    }

}
