package com.zrlog.util;

import javax.servlet.http.HttpServletRequest;

/**
 * copy from org.apache.catalina.util;
 */
public final class RequestUtil {
    private RequestUtil() {
    }

    public static String getRequestUriWithQueryString(HttpServletRequest request) {
        return request.getRequestURI() +
                (request.getQueryString() != null ? "?" + request.getQueryString() : "");
    }
}
