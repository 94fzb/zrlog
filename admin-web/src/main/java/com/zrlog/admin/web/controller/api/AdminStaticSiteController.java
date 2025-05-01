package com.zrlog.admin.web.controller.api;

import com.hibegin.http.annotation.ResponseBody;
import com.hibegin.http.server.execption.NotFindResourceException;
import com.zrlog.admin.business.rest.response.AdminApiPageDataStandardResponse;
import com.zrlog.admin.business.rest.response.AdminStaticSiteSyncResponse;
import com.zrlog.admin.web.plugin.AdminStaticResourcePlugin;
import com.zrlog.business.plugin.StaticSitePlugin;
import com.zrlog.common.Constants;
import com.zrlog.common.controller.BaseController;

import java.util.Objects;

public class AdminStaticSiteController extends BaseController {

    @ResponseBody
    public AdminApiPageDataStandardResponse<AdminStaticSiteSyncResponse> startSync() {
        if (StaticSitePlugin.isDisabled()) {
            return new AdminApiPageDataStandardResponse<>(new AdminStaticSiteSyncResponse(false));
        }
        AdminStaticResourcePlugin adminStaticResourcePlugin = Constants.zrLogConfig.getPlugin(AdminStaticResourcePlugin.class);
        if (Objects.isNull(adminStaticResourcePlugin)) {
            throw new NotFindResourceException("plugin not found");
        }
        adminStaticResourcePlugin.start();
        return new AdminApiPageDataStandardResponse<>(new AdminStaticSiteSyncResponse(adminStaticResourcePlugin.waitCacheSync(request)));
    }

    @ResponseBody
    public AdminApiPageDataStandardResponse<AdminStaticSiteSyncResponse> index() {
        if (StaticSitePlugin.isDisabled()) {
            return new AdminApiPageDataStandardResponse<>(new AdminStaticSiteSyncResponse(false));
        }
        AdminStaticResourcePlugin adminStaticResourcePlugin = Constants.zrLogConfig.getPlugin(AdminStaticResourcePlugin.class);
        if (Objects.isNull(adminStaticResourcePlugin)) {
            throw new NotFindResourceException("plugin not found");
        }
        return new AdminApiPageDataStandardResponse<>(new AdminStaticSiteSyncResponse(adminStaticResourcePlugin.isSynchronized(request)));
    }
}
