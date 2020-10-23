package com.zrlog.web.controller.blog;

import com.jfinal.core.Controller;
import com.zrlog.common.Constants;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.ZrLogUtil;
import com.zrlog.web.config.ZrLogConfig;

import java.util.HashMap;
import java.util.Map;

public class BlogApiPublicController extends Controller {

    public Map<String, Object> resource() {
        Map<String, Object> stringObjectMap = I18nUtil.threadLocal.get();
        stringObjectMap.put("currentVersion", BlogBuildInfoUtil.getVersion());
        stringObjectMap.put("websiteTitle", Constants.WEB_SITE.get("title"));
        stringObjectMap.put("articleRoute", Constants.getArticleRoute());
        if (ZrLogUtil.isPreviewMode()) {
            Map<String, String> defaultLoginInfo = new HashMap<>();
            defaultLoginInfo.put("userName", System.getenv("DEFAULT_USERNAME"));
            defaultLoginInfo.put("password", System.getenv("DEFAULT_PASSWORD"));
            stringObjectMap.put("defaultLoginInfo", defaultLoginInfo);
        }
        return stringObjectMap;
    }

    public Map<String, Boolean> installed() {
        Map<String, Boolean> result = new HashMap<>();
        result.put("installed", ZrLogConfig.isInstalled());
        return result;
    }
}
