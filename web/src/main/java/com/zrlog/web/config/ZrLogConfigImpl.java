package com.zrlog.web.config;

import com.hibegin.common.dao.DataSourceWrapper;
import com.hibegin.http.server.api.HttpRequest;
import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import com.zrlog.business.plugin.CacheManagerPlugin;
import com.zrlog.business.plugin.PluginCorePluginImpl;
import com.zrlog.business.service.DbUpgradeService;
import com.zrlog.common.Updater;
import com.zrlog.common.ZrLogConfig;
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

    public ZrLogConfigImpl(Integer port, Updater updater, String contextPath) {
        super(port, updater, contextPath);
        SetupConfig setupConfig = new SetupConfig(this, dbPropertiesFile, installLockFile, contextPath, webSetups, updater);
        this.tokenService = setupConfig.getAdminTokenService();
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
    public Plugins getBasePluginList() {
        Plugins plugins = new Plugins();
        if (isTest()) {
            return plugins;
        }
        try {
            plugins.add(new PluginCorePluginImpl(dbPropertiesFile));
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

    @Override
    public void refreshPluginCacheData(String version, HttpRequest request) {
        PluginUtils.refreshPluginCacheData(version, request);
    }

}
