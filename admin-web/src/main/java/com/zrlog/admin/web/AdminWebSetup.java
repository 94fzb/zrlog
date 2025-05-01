package com.zrlog.admin.web;

import com.hibegin.http.server.api.Interceptor;
import com.zrlog.web.WebSetup;
import com.zrlog.admin.web.config.AdminRouters;
import com.zrlog.admin.web.interceptor.AdminCrossOriginInterceptor;
import com.zrlog.admin.web.interceptor.AdminInterceptor;
import com.zrlog.admin.web.interceptor.AdminPwaInterceptor;
import com.zrlog.admin.web.plugin.UpdateVersionInfoPlugin;
import com.zrlog.common.AdminResource;
import com.zrlog.common.ZrLogConfig;
import com.zrlog.plugin.Plugins;

import java.util.List;

public class AdminWebSetup implements WebSetup {

    private final ZrLogConfig zrLogConfig;
    private final AdminResource adminResource;

    public AdminWebSetup(ZrLogConfig zrLogConfig, AdminResource adminResource) {
        this.zrLogConfig = zrLogConfig;
        this.adminResource = adminResource;
    }

    @Override
    public void setup() {
        List<Class<? extends Interceptor>> interceptors = zrLogConfig.getServerConfig().getInterceptors();
        //admin
        interceptors.add(AdminPwaInterceptor.class);
        interceptors.add(AdminCrossOriginInterceptor.class);
        interceptors.add(AdminInterceptor.class);
        //router
        AdminRouters.configAdminRoute(zrLogConfig.getServerConfig().getRouter(), adminResource);
    }

    @Override
    public Plugins getPlugins() {
        Plugins iPlugins = new Plugins();
        iPlugins.add(new UpdateVersionInfoPlugin());
        return iPlugins;
    }
}
