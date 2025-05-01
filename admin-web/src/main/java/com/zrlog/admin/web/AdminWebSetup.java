package com.zrlog.admin.web;

import com.hibegin.common.util.EnvKit;
import com.hibegin.http.server.api.Interceptor;
import com.zrlog.admin.business.AdminConstants;
import com.zrlog.admin.web.config.AdminRouters;
import com.zrlog.admin.web.controller.api.AdminController;
import com.zrlog.admin.web.interceptor.AdminCrossOriginInterceptor;
import com.zrlog.admin.web.interceptor.AdminInterceptor;
import com.zrlog.admin.web.interceptor.AdminPluginInterceptor;
import com.zrlog.admin.web.interceptor.AdminPwaInterceptor;
import com.zrlog.admin.web.plugin.AdminStaticResourcePlugin;
import com.zrlog.admin.web.plugin.UpdateVersionInfoPlugin;
import com.zrlog.admin.business.service.AdminResource;
import com.zrlog.common.ZrLogConfig;
import com.zrlog.plugin.Plugins;
import com.zrlog.web.WebSetup;

import java.util.List;

public class AdminWebSetup implements WebSetup {

    private final ZrLogConfig zrLogConfig;
    private final AdminResource adminResource;
    private final String contextPath;

    public AdminWebSetup(ZrLogConfig zrLogConfig, AdminResource adminResource, String contextPath) {
        this.zrLogConfig = zrLogConfig;
        this.adminResource = adminResource;
        this.contextPath = contextPath;
        AdminConstants.adminResource = adminResource;
    }

    @Override
    public void setup() {
        List<Class<? extends Interceptor>> interceptors = zrLogConfig.getServerConfig().getInterceptors();
        //admin
        interceptors.add(AdminPwaInterceptor.class);
        interceptors.add(AdminCrossOriginInterceptor.class);
        interceptors.add(AdminPluginInterceptor.class);
        interceptors.add(AdminInterceptor.class);
        if (EnvKit.isDevMode()) {
            AdminController.configDev(zrLogConfig.getServerConfig());
        }
        //router
        AdminRouters.configAdminRoute(zrLogConfig.getServerConfig().getRouter(), adminResource);
    }

    @Override
    public Plugins getPlugins() {
        Plugins iPlugins = new Plugins();
        iPlugins.add(new UpdateVersionInfoPlugin());
        iPlugins.add(new AdminStaticResourcePlugin(zrLogConfig, adminResource, contextPath));
        return iPlugins;
    }
}
