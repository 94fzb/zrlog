package com.zrlog.admin.util;

import com.hibegin.common.util.StringUtils;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.zrlog.admin.business.AdminConstants;
import com.zrlog.admin.business.exception.AdminAuthException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class AdminWebTools {

    public static void blockUnLoginRequestHandler(HttpRequest request, HttpResponse response) {
        String actionKey = request.getUri();
        if (actionKey.startsWith("/api")) {
            throw new AdminAuthException();
        } else {
            try {
                String url = AdminConstants.ADMIN_LOGIN_URI_PATH + "?redirectFrom="
                        + URLEncoder.encode(getRequestUriWithQueryString(request), StandardCharsets.UTF_8);
                response.redirect(url);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static String getRequestUriWithQueryString(HttpRequest request) {
        String realUri = request.getContextPath() + request.getUri();
        if (request.getUri().endsWith(AdminConstants.ADMIN_URI_BASE_PATH)) {
            realUri = realUri + AdminConstants.INDEX_URI_PATH;
        }
        if (StringUtils.isEmpty(request.getQueryStr())) {
            return realUri;
        }
        return realUri + "?" + request.getQueryStr();
    }
}
