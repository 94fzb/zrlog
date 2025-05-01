package com.zrlog.util;

import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.zrlog.common.Constants;

import java.util.Objects;

public class CrossUtils {

    public static boolean isEnableOrigin(HttpRequest request) {
        String origin = request.getHeader("Origin");
        return Constants.isStaticHtmlStatus() || Objects.nonNull(origin);
    }


    public static void cross(HttpRequest request, HttpResponse response) {
        if (CrossUtils.isEnableOrigin(request)) {
            //可以跨域请求
            response.addHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
            response.addHeader("Access-Control-Allow-Credentials", "true");
        }
    }

}
