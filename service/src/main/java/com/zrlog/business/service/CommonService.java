package com.zrlog.business.service;

import com.hibegin.http.server.api.HttpRequest;
import com.zrlog.business.util.InstallUtils;
import com.zrlog.common.Constants;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.ZrLogUtil;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CommonService {

    public Map<String, Object> blogResourceInfo() {
        Map<String, Object> stringObjectMap = I18nUtil.threadLocal.get().getBlog().get(I18nUtil.getCurrentLocale());
        stringObjectMap.put("currentVersion", BlogBuildInfoUtil.getVersion());
        stringObjectMap.put("websiteTitle", Constants.WEB_SITE.get("title"));
        stringObjectMap.put("articleRoute", "");
        stringObjectMap.put("admin_darkMode", Constants.getBooleanByFromWebSite("admin_darkMode"));
        if (ZrLogUtil.isPreviewMode()) {
            Map<String, String> defaultLoginInfo = new HashMap<>();
            defaultLoginInfo.put("userName", System.getenv("DEFAULT_USERNAME"));
            defaultLoginInfo.put("password", System.getenv("DEFAULT_PASSWORD"));
            stringObjectMap.put("defaultLoginInfo", defaultLoginInfo);
        }
        stringObjectMap.put("admin_color_primary", Objects.toString(Constants.WEB_SITE.get("admin_color_primary"), "#1677ff"));
        return stringObjectMap;
    }

    public Map<String, Object> installResourceInfo(HttpRequest request) {
        Map<String, Object> stringObjectMap =
                I18nUtil.threadLocal.get().getInstall().get(I18nUtil.getAcceptLocal(request));
        stringObjectMap.put("currentVersion", BlogBuildInfoUtil.getVersion());
        //encoding ok, remove utfTips
        if (Charset.defaultCharset().displayName().toLowerCase().contains("utf")) {
            stringObjectMap.put("utfTips", "");
        }
        stringObjectMap.put("installed", InstallUtils.isInstalled());
        return stringObjectMap;
    }
}
