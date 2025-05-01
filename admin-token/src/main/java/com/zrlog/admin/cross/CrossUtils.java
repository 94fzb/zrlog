package com.zrlog.admin.cross;

import com.hibegin.http.server.api.HttpRequest;
import com.zrlog.common.Constants;

import java.util.Objects;

public class CrossUtils {

    public static boolean isEnableOrigin(HttpRequest request) {
        String origin = request.getHeader("Origin");
        return Constants.isStaticHtmlStatus() || Objects.nonNull(origin);
    }
}
