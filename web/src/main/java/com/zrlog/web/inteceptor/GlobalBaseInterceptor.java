package com.zrlog.web.inteceptor;

import com.hibegin.common.util.LoggerUtil;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.hibegin.http.server.api.Interceptor;
import com.zrlog.admin.web.controller.api.AdminController;
import com.zrlog.blog.web.util.WebTools;
import com.zrlog.common.Constants;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.ZrLogUtil;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

/**
 * 用于对静态文件的请求的检查
 */
public class GlobalBaseInterceptor implements Interceptor {

    private static final Logger LOGGER = LoggerUtil.getLogger(AdminController.class);

    public GlobalBaseInterceptor() {
    }

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
        try {
            String target = request.getUri();
            request.getAttr().put("requrl", ZrLogUtil.getFullUrl(request));
            request.getAttr().put("reqUriPath", Objects.requireNonNullElse(request.getUri(), "/"));
            request.getAttr().put("reqQueryString", Objects.requireNonNullElse(request.getQueryStr(), ""));
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
            request.getAttr().put("baseWithHostPath", ZrLogUtil.getHomeUrlWithHostNotProtocol(request));
            return true;
        } finally {
            long used = System.currentTimeMillis() - request.getCreateTime();
            if (used > 5000) {
                LOGGER.info("Slow request " + request.getUri() + " used time " + used + "ms");
            } else if (Constants.debugLoggerPrintAble()) {
                LOGGER.info("Request " + request.getUri() + " used time " + used + "ms");
            }
        }
    }

}
