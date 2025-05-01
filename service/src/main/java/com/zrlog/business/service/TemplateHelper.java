package com.zrlog.business.service;

import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.web.cookie.Cookie;
import com.zrlog.common.Constants;
import com.zrlog.util.ZrLogUtil;

import java.util.Objects;

public class TemplateHelper {

    public static String getNavUrl(HttpRequest request, String suffix, String url) {
        if ("/".equals(url)) {
            return ZrLogUtil.getHomeUrlWithHost(request);
        }
        //文章页
        if (url.startsWith("/")) {
            String nUrl = ZrLogUtil.getHomeUrlWithHost(request) + url.substring(1);
            if (Objects.nonNull(suffix) && !suffix.trim().isEmpty() && nUrl.endsWith(suffix)) {
                return nUrl;
            }
            if (Objects.equals("/admin/login", url)) {
                return nUrl;
            }
            return nUrl + suffix;
        }
        return url;
    }

    public static String getTemplatePathByCookie(Cookie[] cookies) {
        String previewTemplate = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("template".equals(cookie.getName()) && cookie.getValue().startsWith(Constants.TEMPLATE_BASE_PATH)) {
                    previewTemplate = cookie.getValue();
                    break;
                }
            }
        }
        return previewTemplate;
    }


}
