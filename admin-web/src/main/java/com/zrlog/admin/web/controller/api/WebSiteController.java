package com.zrlog.admin.web.controller.api;

import com.hibegin.common.util.BeanUtil;
import com.hibegin.http.annotation.ResponseBody;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.hibegin.http.server.web.Controller;
import com.zrlog.admin.business.rest.base.BasicWebSiteInfo;
import com.zrlog.admin.business.rest.base.BlogWebSiteInfo;
import com.zrlog.admin.business.rest.base.OtherWebSiteInfo;
import com.zrlog.admin.business.rest.base.UpgradeWebSiteInfo;
import com.zrlog.admin.business.rest.response.VersionResponse;
import com.zrlog.admin.business.rest.response.WebSiteSettingsResponse;
import com.zrlog.admin.business.service.WebSiteService;
import com.zrlog.admin.web.annotation.RefreshCache;
import com.zrlog.admin.web.plugin.UpdateVersionPlugin;
import com.zrlog.common.Constants;
import com.zrlog.common.rest.response.ApiStandardResponse;
import com.zrlog.common.type.AutoUpgradeVersionType;
import com.zrlog.model.WebSite;
import com.zrlog.plugin.IPlugin;
import com.zrlog.plugin.Plugins;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.I18nUtil;

import java.sql.SQLException;
import java.util.Map;
import java.util.Map.Entry;

public class WebSiteController extends Controller {

    private final WebSiteService webSiteService = new WebSiteService();

    public WebSiteController() {
    }

    public WebSiteController(HttpRequest request, HttpResponse response) {
        super(request, response);
    }

    @ResponseBody
    public ApiStandardResponse<VersionResponse> version() {
        VersionResponse versionResponse = new VersionResponse();
        versionResponse.setBuildId(BlogBuildInfoUtil.getBuildId());
        versionResponse.setVersion(BlogBuildInfoUtil.getVersion());
        versionResponse.setChangelog(UpdateVersionPlugin.getChangeLog(BlogBuildInfoUtil.getVersion(),BlogBuildInfoUtil.getTime(),
                BlogBuildInfoUtil.getBuildId(), I18nUtil.getBackend()));
        return new ApiStandardResponse<>(versionResponse);
    }

    @ResponseBody
    public ApiStandardResponse<WebSiteSettingsResponse> index() {
        WebSiteSettingsResponse webSiteSettingsResponse = webSiteService.loadSettings();
        webSiteSettingsResponse.setTemplates(new TemplateController(request, response).index().getData());
        return new ApiStandardResponse<>(webSiteSettingsResponse);
    }

    @RefreshCache
    @ResponseBody
    public ApiStandardResponse<Void> basic() throws SQLException {
        return update(BeanUtil.convertWithValid(getRequest().getInputStream(), BasicWebSiteInfo.class));
    }

    private ApiStandardResponse<Void> update(Object t) throws SQLException {
        Map<String, Object> requestMap = BeanUtil.convert(t, Map.class);
        for (Entry<String, Object> param : requestMap.entrySet()) {
            new WebSite().updateByKV(param.getKey(), param.getValue());
        }
        ApiStandardResponse<Void> updateResponse = new ApiStandardResponse<>();
        updateResponse.setError(0);
        updateResponse.setMessage(I18nUtil.getBackendStringFromRes("updateSuccess"));
        return updateResponse;
    }

    @RefreshCache
    @ResponseBody
    public ApiStandardResponse<Void> blog() throws SQLException {
        return update(BeanUtil.convertWithValid(getRequest().getInputStream(), BlogWebSiteInfo.class));
    }

    @RefreshCache
    @ResponseBody
    public ApiStandardResponse<Void> other() throws SQLException {
        return update(BeanUtil.convertWithValid(getRequest().getInputStream(), OtherWebSiteInfo.class));
    }

    @RefreshCache
    @ResponseBody
    public ApiStandardResponse<Void> upgrade() throws SQLException {
        UpgradeWebSiteInfo request = BeanUtil.convertWithValid(getRequest().getInputStream(), UpgradeWebSiteInfo.class);
        if (AutoUpgradeVersionType.cycle(request.getAutoUpgradeVersion().intValue()) == AutoUpgradeVersionType.NEVER) {
            for (IPlugin plugin : Constants.plugins) {
                if (plugin instanceof UpdateVersionPlugin) {
                    plugin.stop();
                }
            }
        } else {
            for (IPlugin plugin : Constants.plugins) {
                if (plugin instanceof UpdateVersionPlugin) {
                    plugin.start();
                }
            }
        }
        return update(request);
    }
}
