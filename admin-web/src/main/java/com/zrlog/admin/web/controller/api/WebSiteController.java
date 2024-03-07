package com.zrlog.admin.web.controller.api;

import com.hibegin.common.util.BeanUtil;
import com.hibegin.common.util.StringUtils;
import com.hibegin.http.annotation.ResponseBody;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.hibegin.http.server.web.Controller;
import com.zrlog.admin.business.rest.base.BasicWebSiteRequest;
import com.zrlog.admin.business.rest.base.BlogWebSiteRequest;
import com.zrlog.admin.business.rest.base.OtherWebSiteRequest;
import com.zrlog.admin.business.rest.base.UpgradeWebSiteRequest;
import com.zrlog.admin.business.rest.response.VersionResponse;
import com.zrlog.admin.business.rest.response.WebSiteSettingsResponse;
import com.zrlog.admin.business.service.WebSiteService;
import com.zrlog.admin.web.annotation.RefreshCache;
import com.zrlog.admin.web.plugin.UpdateVersionPlugin;
import com.zrlog.common.rest.response.ApiStandardResponse;
import com.zrlog.model.WebSite;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.ZrLogUtil;

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
        versionResponse.setChangelog(UpdateVersionPlugin.getChangeLog(BlogBuildInfoUtil.getVersion(),
                BlogBuildInfoUtil.getBuildId()));
        return new ApiStandardResponse<>(versionResponse);
    }

    @ResponseBody
    public ApiStandardResponse<WebSiteSettingsResponse> index() {
        WebSiteSettingsResponse webSiteSettingsResponse = webSiteService.loadSettings();
        webSiteSettingsResponse.setTemplates(new TemplateController(request,response).index().getData());
        return new ApiStandardResponse<>(webSiteSettingsResponse);
    }

    @RefreshCache
    @ResponseBody
    public ApiStandardResponse<Void> basic() throws SQLException {
        return update(BasicWebSiteRequest.class);
    }

    private ApiStandardResponse<Void> update(Class<?> t) throws SQLException {
        Map<String, Object> requestMap = BeanUtil.convert(ZrLogUtil.convertRequestBody(getRequest(), t), Map.class);
        for (Entry<String, Object> param : requestMap.entrySet()) {
            new WebSite().updateByKV(param.getKey(), param.getValue());
        }
        ApiStandardResponse<Void> updateResponse = new ApiStandardResponse<>();
        updateResponse.setError(0);
        updateResponse.setMessage(I18nUtil.getBlogStringFromRes("updateSuccess"));
        return updateResponse;
    }

    @RefreshCache
    @ResponseBody
    public ApiStandardResponse<Void> blog() throws SQLException {
        return update(BlogWebSiteRequest.class);
    }

    @RefreshCache
    @ResponseBody
    public ApiStandardResponse<Void> other() throws SQLException {
        return update(OtherWebSiteRequest.class);
    }

    @RefreshCache
    @ResponseBody
    public ApiStandardResponse<Void> upgrade() throws SQLException {
        UpgradeWebSiteRequest request = ZrLogUtil.convertRequestBody(getRequest(), UpgradeWebSiteRequest.class);
        Map<String, Object> requestMap = BeanUtil.convert(request, Map.class);
        for (Entry<String, Object> param : requestMap.entrySet()) {
            new WebSite().updateByKV(param.getKey(), param.getValue());
        }
        /*Plugins plugins = (Plugins) .me().getServletContext().getAttribute("plugins");
        if (AutoUpgradeVersionType.cycle(request.getAutoUpgradeVersion().intValue()) == AutoUpgradeVersionType.NEVER) {
            for (IPlugin plugin : plugins.getPluginList()) {
                if (plugin instanceof UpdateVersionPlugin) {
                    plugin.stop();
                }
            }
        } else {
            for (IPlugin plugin : plugins.getPluginList()) {
                if (plugin instanceof UpdateVersionPlugin) {
                    plugin.start();
                }
            }
        }*/
        ApiStandardResponse<Void> recordResponse = new ApiStandardResponse<>();
        recordResponse.setError(0);
        recordResponse.setMessage(I18nUtil.getBlogStringFromRes("updateSuccess"));
        return recordResponse;
    }
}
