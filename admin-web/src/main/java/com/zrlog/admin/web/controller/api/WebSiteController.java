package com.zrlog.admin.web.controller.api;

import com.hibegin.common.util.BeanUtil;
import com.hibegin.http.HttpMethod;
import com.hibegin.http.annotation.ResponseBody;
import com.zrlog.admin.business.rest.base.*;
import com.zrlog.admin.business.rest.response.AdminApiPageDataStandardResponse;
import com.zrlog.admin.business.rest.response.VersionResponse;
import com.zrlog.admin.business.service.WebSiteService;
import com.zrlog.admin.web.annotation.RefreshCache;
import com.zrlog.admin.web.plugin.UpdateVersionInfoPlugin;
import com.zrlog.admin.web.type.AutoUpgradeVersionType;
import com.zrlog.common.Constants;
import com.zrlog.common.controller.BaseController;
import com.zrlog.model.WebSite;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.I18nUtil;

import java.sql.SQLException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

public class WebSiteController extends BaseController {

    private final WebSiteService webSiteService = new WebSiteService();

    @ResponseBody
    public AdminApiPageDataStandardResponse<VersionResponse> version() {
        VersionResponse versionResponse = new VersionResponse();
        versionResponse.setBuildId(BlogBuildInfoUtil.getBuildId());
        versionResponse.setVersion(BlogBuildInfoUtil.getVersion());
        versionResponse.setChangelog(UpdateVersionInfoPlugin.getCurrentChangeLog(I18nUtil.getBackend()));
        return new AdminApiPageDataStandardResponse<>(versionResponse);
    }

    @ResponseBody
    public AdminApiPageDataStandardResponse<BasicWebSiteInfo> index() throws SQLException {
        return basic();
    }

    @RefreshCache(onlyOnPostMethod = true)
    @ResponseBody
    public AdminApiPageDataStandardResponse<BasicWebSiteInfo> basic() throws SQLException {
        if (request.getMethod() == HttpMethod.POST) {
            update(getRequestBodyWithNullCheck(BasicWebSiteInfo.class));
        }
        return new AdminApiPageDataStandardResponse<>(webSiteService.basicWebSiteInfo(), I18nUtil.getBackendStringFromRes("updateSuccess"));

    }

    private AdminApiPageDataStandardResponse<Void> update(Object t) throws SQLException {
        Map<String, Object> requestMap = BeanUtil.convert(t, Map.class);
        if (Objects.nonNull(requestMap)) {
            for (Entry<String, Object> param : requestMap.entrySet()) {
                new WebSite().updateByKV(param.getKey(), param.getValue());
            }
        }
        AdminApiPageDataStandardResponse<Void> updateResponse = new AdminApiPageDataStandardResponse<>();
        updateResponse.setError(0);
        updateResponse.setMessage(I18nUtil.getBackendStringFromRes("updateSuccess"));
        return updateResponse;
    }

    @RefreshCache(onlyOnPostMethod = true)
    @ResponseBody
    public AdminApiPageDataStandardResponse<BlogWebSiteInfo> blog() throws SQLException {
        if (request.getMethod() == HttpMethod.POST) {
            update(getRequestBodyWithNullCheck(BlogWebSiteInfo.class));
        }
        return new AdminApiPageDataStandardResponse<>(webSiteService.blogWebSiteInfo(), I18nUtil.getBackendStringFromRes("updateSuccess"));
    }

    @RefreshCache(onlyOnPostMethod = true)
    @ResponseBody
    public AdminApiPageDataStandardResponse<OtherWebSiteInfo> other() throws SQLException {
        if (request.getMethod() == HttpMethod.POST) {
            update(getRequestBodyWithNullCheck(OtherWebSiteInfo.class));
        }
        return new AdminApiPageDataStandardResponse<>(webSiteService.other(), I18nUtil.getBackendStringFromRes("updateSuccess"));
    }

    /**
     * admin 管理的修改，不应该引起博客数据的变化，所以无需更新缓存
     *
     * @return
     * @throws SQLException
     */
    @RefreshCache(onlyOnPostMethod = true)
    @ResponseBody
    public AdminApiPageDataStandardResponse<AdminWebSiteInfo> admin() throws SQLException {
        if (request.getMethod() == HttpMethod.POST) {
            update(getRequestBodyWithNullCheck(AdminWebSiteInfo.class));
        }
        return new AdminApiPageDataStandardResponse<>(webSiteService.adminWebSiteInfo(request), I18nUtil.getBackendStringFromRes("updateSuccess"));
    }

    @RefreshCache(onlyOnPostMethod = true)
    @ResponseBody
    public AdminApiPageDataStandardResponse<UpgradeWebSiteInfo> upgrade() throws SQLException {
        if (request.getMethod() == HttpMethod.POST) {
            UpgradeWebSiteInfo request = getRequestBodyWithNullCheck(UpgradeWebSiteInfo.class);
            update(request);
            if (AutoUpgradeVersionType.cycle(request.getAutoUpgradeVersion().intValue()) == AutoUpgradeVersionType.NEVER) {
                UpdateVersionInfoPlugin updateVersionInfoPlugin = Constants.zrLogConfig.getPlugin(UpdateVersionInfoPlugin.class);
                if (updateVersionInfoPlugin != null) {
                    updateVersionInfoPlugin.stop();
                }

            } else {
                UpdateVersionInfoPlugin updateVersionInfoPlugin = Constants.zrLogConfig.getPlugin(UpdateVersionInfoPlugin.class);
                if (updateVersionInfoPlugin != null) {
                    updateVersionInfoPlugin.start();
                }
            }
        }
        return new AdminApiPageDataStandardResponse<>(webSiteService.upgradeWebSiteInfo(), I18nUtil.getBackendStringFromRes("updateSuccess"));
    }
}
