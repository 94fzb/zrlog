package com.zrlog.util;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * copy from org.apache.catalina.util;
 */
public final class RequestUtil {
    private RequestUtil() {
    }

    public static StringBuffer getRequestURLWithQueryString(HttpServletRequest request) {
        StringBuffer url = new StringBuffer();
        String scheme = request.getScheme();
        if (Objects.equals(request.getHeader("X-Forwarded-Proto"), "https")) {
            scheme = "https";
        }
        int port = request.getServerPort();
        if (port < 0) {
            port = 80;
        }

        url.append(scheme);
        url.append("://");
        url.append(request.getServerName());
        if (scheme.equals("http") && port != 80 || scheme.equals("https") && (port != 443 && port != 80)) {
            url.append(':');
            url.append(port);
        }

        url.append(request.getRequestURI());
        url.append(request.getQueryString() != null ? "?" + request.getQueryString() : "");
        return url;
    }
}
