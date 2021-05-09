package com.zrlog.blog.web.controller.api;

import com.jfinal.core.Controller;
import com.zrlog.business.util.InstallUtils;
import com.zrlog.common.Constants;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.ZrLogUtil;
import com.zrlog.blog.web.util.WebTools;

import java.util.HashMap;
import java.util.Map;

public class BlogApiPublicController extends Controller {

    public Map<String, Object> resource() {
        Map<String, Object> stringObjectMap = I18nUtil.threadLocal.get();
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
        stringObjectMap.put("templateDownloadFromUrl", "https://store.zrlog.com/template/?from=http:" + WebTools.getHomeUrlWithHost(getRequest()) +
                "admin/template&v=" + BlogBuildInfoUtil.getVersion() +
                "&id=" + BlogBuildInfoUtil.getBuildId());
        return stringObjectMap;
    }

    public Map<String, Boolean> installed() {
        Map<String, Boolean> result = new HashMap<>();
        result.put("installed", InstallUtils.isInstalled());
        return result;
    }
}
