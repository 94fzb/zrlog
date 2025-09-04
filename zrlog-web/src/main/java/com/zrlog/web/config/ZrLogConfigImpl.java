package com.zrlog.web.config;

import com.hibegin.common.dao.DataSourceWrapper;
import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import com.zrlog.business.plugin.CacheManagerPlugin;
import com.zrlog.business.plugin.PluginCorePluginImpl;
import com.zrlog.business.service.DbUpgradeService;
import com.zrlog.common.TokenService;
import com.zrlog.common.Updater;
import com.zrlog.common.ZrLogConfig;
import com.zrlog.common.vo.PublicWebSiteInfo;
import com.zrlog.data.cache.CacheServiceImpl;
import com.zrlog.plugin.Plugins;
import com.zrlog.web.WebSetup;
import com.zrlog.web.inteceptor.DefaultInterceptor;

import java.util.Objects;
import java.util.logging.Level;

/**
 * 核心一些参数的配置。
 */
public class ZrLogConfigImpl extends ZrLogConfig {

    private final SetupConfig setupConfig;

    public ZrLogConfigImpl(Integer port, Updater updater, String contextPath) {
        super(port, updater, contextPath);
        this.setupConfig = new SetupConfig(this, dbPropertiesFile, installLockFile, contextPath, webSetups, updater);
        //config
        this.webSetups.forEach(WebSetup::setup);
        if (!setupConfig.isIncludeBlog()) {
            serverConfig.getInterceptors().add(DefaultInterceptor.class);
        }
    }

    @Override
    public DataSourceWrapper configDatabase() throws Exception {
        this.dataSource = super.configDatabase();
        if (Objects.nonNull(dataSource)) {
            this.cacheService = new CacheServiceImpl();
            new DbUpgradeService(this.dataSource, this.cacheService.getCurrentSqlVersion()).tryDoUpgrade();
        }
        return dataSource;
    }

    @Override
    protected TokenService initTokenService() {
        if (Objects.isNull(cacheService)) {
            return null;
        }
        PublicWebSiteInfo publicWebSiteInfo = this.cacheService.getPublicWebSiteInfo();
        return setupConfig.buildAdminTokenService(publicWebSiteInfo.getSession_timeout());
    }

    @Override
    public Plugins getBasePluginList() {
        Plugins plugins = new Plugins();
        if (isTest()) {
            return plugins;
        }
        try {
            plugins.add(new PluginCorePluginImpl(dbPropertiesFile, serverConfig.getContextPath()));
            plugins.add(new CacheManagerPlugin(this));
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "configPlugin exception ", e);
        }
        return plugins;
    }

    @Override
    public void stop() {
        super.stop();
        if (Objects.nonNull(this.dataSource)) {
            if (this.dataSource.isWebApi()) {
                return;
            }
        }
        AbandonedConnectionCleanupThread.checkedShutdown();
    }
}
