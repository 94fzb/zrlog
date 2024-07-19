package com.zrlog.admin.web.controller.api;

import com.hibegin.common.util.BeanUtil;
import com.hibegin.common.util.StringUtils;
import com.hibegin.http.HttpMethod;
import com.hibegin.http.annotation.ResponseBody;
import com.hibegin.http.server.web.Controller;
import com.zrlog.admin.business.rest.base.*;
import com.zrlog.admin.business.rest.response.VersionResponse;
import com.zrlog.admin.business.service.WebSiteService;
import com.zrlog.admin.web.annotation.RefreshCache;
import com.zrlog.admin.web.plugin.UpdateVersionPlugin;
import com.zrlog.common.Constants;
import com.zrlog.common.rest.response.ApiStandardResponse;
import com.zrlog.common.type.AutoUpgradeVersionType;
import com.zrlog.model.WebSite;
import com.zrlog.plugin.IPlugin;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.I18nUtil;

import java.sql.SQLException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

public class WebSiteController extends Controller {

    private final WebSiteService webSiteService = new WebSiteService();

    @ResponseBody
    public ApiStandardResponse<VersionResponse> version() {
        VersionResponse versionResponse = new VersionResponse();
        versionResponse.setBuildId(BlogBuildInfoUtil.getBuildId());
        versionResponse.setVersion(BlogBuildInfoUtil.getVersion());
        versionResponse.setChangelog(UpdateVersionPlugin.getChangeLog(BlogBuildInfoUtil.getVersion(), BlogBuildInfoUtil.getTime(),
                BlogBuildInfoUtil.getBuildId(), I18nUtil.getBackend()));
        String requestBuildId = request.getParaToStr("buildId");
        if (StringUtils.isNotEmpty(requestBuildId) && Objects.equals(versionResponse.getBuildId(), requestBuildId)) {
            versionResponse.setMessage(I18nUtil.getBackendStringFromRes("upgradeSuccess"));
        }
        return new ApiStandardResponse<>(versionResponse);
    }

    @ResponseBody
    public ApiStandardResponse<BasicWebSiteInfo> index() throws SQLException {
        return basic();
    }

    @RefreshCache(onlyOnPostMethod = true)
    @ResponseBody
    public ApiStandardResponse<BasicWebSiteInfo> basic() throws SQLException {
        if (request.getMethod() == HttpMethod.POST) {
            update(BeanUtil.convertWithValid(getRequest().getInputStream(), BasicWebSiteInfo.class));
        }
        return new ApiStandardResponse<>(webSiteService.basicWebSiteInfo(), I18nUtil.getBackendStringFromRes("updateSuccess"));

    }

    private ApiStandardResponse<Void> update(Object t) throws SQLException {
        Map<String, Object> requestMap = BeanUtil.convert(t, Map.class);
        if (Objects.nonNull(requestMap)) {
            for (Entry<String, Object> param : requestMap.entrySet()) {
                new WebSite().updateByKV(param.getKey(), param.getValue());
            }
        }
        ApiStandardResponse<Void> updateResponse = new ApiStandardResponse<>();
        updateResponse.setError(0);
        Constants.zrLogConfig.getCacheService().refreshWebSite();
        updateResponse.setMessage(I18nUtil.getBackendStringFromRes("updateSuccess"));
        return updateResponse;
    }

    @RefreshCache(onlyOnPostMethod = true)
    @ResponseBody
    public ApiStandardResponse<BlogWebSiteInfo> blog() throws SQLException {
        if (request.getMethod() == HttpMethod.POST) {
            update(BeanUtil.convertWithValid(getRequest().getInputStream(), BlogWebSiteInfo.class));
        }
        return new ApiStandardResponse<>(webSiteService.blogWebSiteInfo(), I18nUtil.getBackendStringFromRes("updateSuccess"));
    }

    @RefreshCache(onlyOnPostMethod = true)
    @ResponseBody
    public ApiStandardResponse<OtherWebSiteInfo> other() throws SQLException {
        if (request.getMethod() == HttpMethod.POST) {
            update(BeanUtil.convertWithValid(getRequest().getInputStream(), OtherWebSiteInfo.class));
        }
        return new ApiStandardResponse<>(webSiteService.other(), I18nUtil.getBackendStringFromRes("updateSuccess"));
    }

    /**
     * admin 管理的修改，不应该引起博客数据的变化，所以无需更新缓存
     *
     * @return
     * @throws SQLException
     */
    @RefreshCache(onlyOnPostMethod = true)
    @ResponseBody
    public ApiStandardResponse<AdminWebSiteInfo> admin() throws SQLException {
        if (request.getMethod() == HttpMethod.POST) {
            update(BeanUtil.convertWithValid(getRequest().getInputStream(), AdminWebSiteInfo.class));
            Constants.zrLogConfig.getCacheService().refreshWebSite();
        }

        return new ApiStandardResponse<>(webSiteService.adminWebSiteInfo(), I18nUtil.getBackendStringFromRes("updateSuccess"));
    }

    @RefreshCache(onlyOnPostMethod = true)
    @ResponseBody
    public ApiStandardResponse<UpgradeWebSiteInfo> upgrade() throws SQLException {
        if (request.getMethod() == HttpMethod.POST) {
            UpgradeWebSiteInfo request = BeanUtil.convertWithValid(getRequest().getInputStream(), UpgradeWebSiteInfo.class);
            update(request);
            if (AutoUpgradeVersionType.cycle(request.getAutoUpgradeVersion().intValue()) == AutoUpgradeVersionType.NEVER) {
                for (IPlugin plugin : Constants.zrLogConfig.getPlugins()) {
                    if (plugin instanceof UpdateVersionPlugin) {
                        plugin.stop();
                    }
                }
            } else {
                for (IPlugin plugin : Constants.zrLogConfig.getPlugins()) {
                    if (plugin instanceof UpdateVersionPlugin) {
                        plugin.start();
                    }
                }
            }
        }
        return new ApiStandardResponse<>(webSiteService.upgradeWebSiteInfo(), I18nUtil.getBackendStringFromRes("updateSuccess"));
    }
}
