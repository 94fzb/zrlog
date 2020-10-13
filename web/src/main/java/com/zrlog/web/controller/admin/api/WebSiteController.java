package com.zrlog.web.controller.admin.api;

import com.zrlog.business.rest.response.VersionResponse;
import com.zrlog.business.rest.response.WebSiteSettingUpdateResponse;
import com.zrlog.business.rest.response.WebSiteSettingsResponse;
import com.zrlog.business.service.WebSiteService;
import com.zrlog.model.WebSite;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.ZrLogUtil;
import com.zrlog.web.annotation.RefreshCache;
import com.zrlog.web.controller.BaseController;
import com.zrlog.web.plugin.UpdateVersionPlugin;

import java.util.Map;
import java.util.Map.Entry;

public class WebSiteController extends BaseController {

    private final WebSiteService webSiteService = new WebSiteService();

    @RefreshCache
    public WebSiteSettingUpdateResponse update() {
        Map<String, Object> requestMap = ZrLogUtil.convertRequestBody(getRequest(), Map.class);
        for (Entry<String, Object> param : requestMap.entrySet()) {
            new WebSite().updateByKV(param.getKey(), param.getValue());
        }

        WebSiteSettingUpdateResponse updateResponse = new WebSiteSettingUpdateResponse();
        updateResponse.setError(0);
        return updateResponse;
    }

    public VersionResponse version() {
        VersionResponse versionResponse = new VersionResponse();
        versionResponse.setBuildId(BlogBuildInfoUtil.getBuildId());
        versionResponse.setVersion(BlogBuildInfoUtil.getVersion());
        versionResponse.setChangelog(UpdateVersionPlugin.getChangeLog(BlogBuildInfoUtil.getVersion(), BlogBuildInfoUtil.getBuildId()));
        return versionResponse;
    }

    public WebSiteSettingsResponse settings() {
        return webSiteService.loadSettings();
    }
}
