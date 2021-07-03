package com.zrlog.business.service;

import com.zrlog.common.Constants;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.ZrLogUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class CommonService {

    public Map<String, Object> blogResourceInfo() {
        Map<String, Object> stringObjectMap = I18nUtil.threadLocal.get().getBlog().get(I18nUtil.getCurrentLocale());
        stringObjectMap.put("currentVersion", BlogBuildInfoUtil.getVersion());
        stringObjectMap.put("websiteTitle", Constants.WEB_SITE.get("title"));
        stringObjectMap.put("articleRoute", Constants.getArticleRoute());
        stringObjectMap.put("admin_darkMode", Constants.getBooleanByFromWebSite("admin_darkMode"));
        if (ZrLogUtil.isPreviewMode()) {
            Map<String, String> defaultLoginInfo = new HashMap<>();
            defaultLoginInfo.put("userName", System.getenv("DEFAULT_USERNAME"));
            defaultLoginInfo.put("password", System.getenv("DEFAULT_PASSWORD"));
            stringObjectMap.put("defaultLoginInfo", defaultLoginInfo);
        }
        return stringObjectMap;
    }

    public Map<String, Object> installResourceInfo(HttpServletRequest request) {
        Map<String, Object> stringObjectMap =
                I18nUtil.threadLocal.get().getInstall().get(I18nUtil.getAcceptLocal(request));
        stringObjectMap.put("currentVersion", BlogBuildInfoUtil.getVersion());
        //encoding ok, remove utfTips
        if (System.getProperty("file.encoding").toLowerCase().contains("utf")) {
            stringObjectMap.put("utfTips", "");
        }
        return stringObjectMap;
    }
}
