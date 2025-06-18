package com.zrlog.admin.web.interceptor;

import com.hibegin.http.HttpMethod;
import com.hibegin.http.io.LengthByteArrayInputStream;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.zrlog.util.ZrLogUtil;

import java.util.Objects;

public class AdminCrossOriginInterceptor extends AdminInterceptor {

    @Override
    public boolean doInterceptor(HttpRequest request, HttpResponse response) {
        String origin = request.getHeader("Origin");
        if (Objects.nonNull(ZrLogUtil.getBlogHostByWebSite()) && Objects.nonNull(origin)) {
            response.addHeader("Access-Control-Allow-Origin", origin);
            response.addHeader("Access-Control-Allow-Credentials", "true");
            if (request.getMethod() == HttpMethod.OPTIONS) {
                response.addHeader("Access-Control-Allow-Methods", "POST,GET,OPTIONS");
                response.addHeader("Access-Control-Allow-Headers", "Content-Type");
                response.write(new LengthByteArrayInputStream(new byte[0]), 200);
                return false;
            }
        }
        return true;
    }
}
