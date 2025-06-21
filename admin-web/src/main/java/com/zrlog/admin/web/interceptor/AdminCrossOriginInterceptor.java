package com.zrlog.admin.web.interceptor;

import com.hibegin.http.HttpMethod;
import com.hibegin.http.io.LengthByteArrayInputStream;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.zrlog.admin.cross.CrossUtils;

public class AdminCrossOriginInterceptor extends AdminInterceptor {

    @Override
    public boolean doInterceptor(HttpRequest request, HttpResponse response) {
        if (CrossUtils.isEnableOrigin(request)) {
            String origin = request.getHeader("Origin");
            response.addHeader("Access-Control-Allow-Origin", origin);
            response.addHeader("Access-Control-Allow-Credentials", "true");
            response.addHeader("Access-Control-Allow-Headers", "Content-Type");
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
