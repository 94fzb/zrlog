package com.zrlog.util;

import javax.servlet.http.HttpServletRequest;

/**
 * copy from org.apache.catalina.util;
 */
public final class RequestUtil {
    private RequestUtil() {
    }

    public static StringBuffer getRequestUriWithQueryString(HttpServletRequest request) {
        StringBuffer url = new StringBuffer();
        url.append(request.getRequestURI());
        url.append(request.getQueryString() != null ? "?" + request.getQueryString() : "");
        return url;
    }
}
