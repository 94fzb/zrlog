package com.zrlog.admin.business.service;

import com.hibegin.common.util.ObjectHelpers;
import com.hibegin.http.server.api.HttpRequest;
import com.zrlog.business.rest.response.PublicInfoVO;
import com.zrlog.business.service.CommonService;
import com.zrlog.common.Constants;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.ZrLogUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AdminResourceService {

    public Map<String, Object> adminResourceInfo(HttpRequest request) {
        if (Objects.isNull(I18nUtil.threadLocal.get())) {
            return new HashMap<>();
        }
        Map<String, Object> stringObjectMap = ObjectHelpers.requireNonNullElse(I18nUtil.getAdmin().get(I18nUtil.getCurrentLocale()), new HashMap<>());
        PublicInfoVO publicInfoVO = new CommonService().getPublicInfo(request);
        stringObjectMap.put("currentVersion", publicInfoVO.getCurrentVersion());
        stringObjectMap.put("websiteTitle", publicInfoVO.getWebsiteTitle());
        stringObjectMap.put("homeUrl", publicInfoVO.getHomeUrl());
        stringObjectMap.put("articleRoute", "");
        stringObjectMap.put("admin_darkMode", publicInfoVO.getAdmin_darkMode());
        if (ZrLogUtil.isPreviewMode()) {
            Map<String, String> defaultLoginInfo = new HashMap<>();
            defaultLoginInfo.put("userName", System.getenv("DEFAULT_USERNAME"));
            defaultLoginInfo.put("password", System.getenv("DEFAULT_PASSWORD"));
            stringObjectMap.put("defaultLoginInfo", defaultLoginInfo);
        }
        stringObjectMap.put("buildId", BlogBuildInfoUtil.getBuildId());
        stringObjectMap.put("appId", Constants.getAppId());
        stringObjectMap.put("admin_color_primary", publicInfoVO.getAdmin_color_primary());
        stringObjectMap.put("lang", I18nUtil.getCurrentLocale());
        stringObjectMap.put("staticPage", Constants.zrLogConfig.isStaticPluginRequest(request));
        //remove
        stringObjectMap.put("staticPlugin", Constants.zrLogConfig.isStaticPluginRequest(request));
        stringObjectMap.put("admin_static_resource_base_url", ZrLogUtil.getAdminStaticResourceBaseUrlByWebSite(request));
        return stringObjectMap;
    }
}
