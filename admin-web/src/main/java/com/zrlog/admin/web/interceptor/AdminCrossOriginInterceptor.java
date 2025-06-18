package com.zrlog.admin.web.interceptor;

import com.hibegin.http.HttpMethod;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.zrlog.util.ZrLogUtil;

import java.util.Objects;

public class AdminCrossOriginInterceptor extends AdminInterceptor {

    @Override
    public boolean doInterceptor(HttpRequest request, HttpResponse response) {
        if (Objects.nonNull(ZrLogUtil.getBlogHostByWebSite())) {
            if (request.getMethod() == HttpMethod.OPTIONS) {
                response.addHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
                response.addHeader("Access-Control-Allow-Credentials", "true");
                response.addHeader("Access-Control-Allow-Methods", "POST,GET,OPTIONS");
                response.addHeader("Access-Control-Allow-Headers", "Content-Type");
                response.renderCode(200);
                return false;
            }
            response.addHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
            response.addHeader("Access-Control-Allow-Credentials", "true");
        }
        return true;
    }
}
