package com.zrlog.business.service;

import com.hibegin.common.util.ObjectHelpers;
import com.hibegin.http.server.api.HttpRequest;
import com.zrlog.business.plugin.UpdateVersionInfoPlugin;
import com.zrlog.business.rest.response.PublicInfoVO;
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

    public Map<String, Object> blogResourceInfo(HttpRequest request) {
        if (Objects.isNull(I18nUtil.threadLocal.get())) {
            return new HashMap<>();
        }
        Map<String, Object> stringObjectMap = ObjectHelpers.requireNonNullElse(I18nUtil.getBlog().get(I18nUtil.getCurrentLocale()), new HashMap<>());
        PublicInfoVO publicInfoVO = getPublicInfo(request);
        stringObjectMap.put("websiteTitle", publicInfoVO.getWebsiteTitle());
        stringObjectMap.put("homeUrl", publicInfoVO.getHomeUrl());
        stringObjectMap.put("articleRoute", "");
        stringObjectMap.put("admin_darkMode", publicInfoVO.getAdmin_darkMode());
        stringObjectMap.put("buildId", BlogBuildInfoUtil.getBuildId());
        return stringObjectMap;
    }

    public Map<String, Object> version() {
        Map<String, Object> stringObjectMap = new HashMap<>();
        stringObjectMap.put("buildId", BlogBuildInfoUtil.getBuildId());
        return stringObjectMap;
    }

    public Map<String, Object> adminResourceInfo(HttpRequest request) {
        if (Objects.isNull(I18nUtil.threadLocal.get())) {
            return new HashMap<>();
        }
        Map<String, Object> stringObjectMap = ObjectHelpers.requireNonNullElse(I18nUtil.getAdmin().get(I18nUtil.getCurrentLocale()), new HashMap<>());
        PublicInfoVO publicInfoVO = getPublicInfo(request);
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
        stringObjectMap.put("staticPage", ZrLogUtil.isStaticPlugin(request));
        //remove
        stringObjectMap.put("staticPlugin", ZrLogUtil.isStaticPlugin(request));
        stringObjectMap.put("admin_static_resource_base_url", ZrLogUtil.getAdminStaticResourceBaseUrlByWebSite(request));
        return stringObjectMap;
    }

    public PublicInfoVO getPublicInfo(HttpRequest request) {
        Boolean darkMode = Constants.getBooleanByFromWebSite("admin_darkMode");
        String themeColor;
        String adminColor = Objects.toString(Constants.zrLogConfig.getPublicWebSite().get("admin_color_primary"), "#1677ff");
        if (darkMode) {
            themeColor = "#000000";
        } else {
            themeColor = adminColor;
        }
        return new PublicInfoVO(BlogBuildInfoUtil.getVersion(), (String) Constants.zrLogConfig.getPublicWebSite().get("title"), ZrLogUtil.getHomeUrlWithHost(request), darkMode, adminColor, themeColor);
    }

    public Map<String, Object> installResourceInfo(HttpRequest request) {
        Map<String, Object> stringObjectMap = ObjectHelpers.requireNonNullElse(I18nUtil.getInstall().get(I18nUtil.getAcceptLocal(request)), new HashMap<>());
        stringObjectMap.put("currentVersion", BlogBuildInfoUtil.getVersion());
        if (ZrLogUtil.isWarMode()) {
            stringObjectMap.put("installedTips", stringObjectMap.get("installedWarTips"));
        }
        //encoding ok, remove utfTips
        if (Charset.defaultCharset().displayName().toLowerCase().contains("utf")) {
            stringObjectMap.put("utfTips", "");
        }
        stringObjectMap.put("installed", InstallUtils.isInstalled());
        //这个是不需要的
        stringObjectMap.remove("installedWarTips");
        if (!InstallUtils.isInstalled()) {
            UpdateVersionInfoPlugin updateVersionInfoPlugin = new UpdateVersionInfoPlugin();
            try {
                stringObjectMap.put("lastVersionInfo", updateVersionInfoPlugin.getLastVersion(true));
            } catch (Exception e) {
                //ignore
            } finally {
                updateVersionInfoPlugin.stop();
            }
        }
        return stringObjectMap;
    }
}
