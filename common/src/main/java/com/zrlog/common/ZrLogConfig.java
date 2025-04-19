package com.zrlog.common;

import com.hibegin.http.server.config.AbstractServerConfig;
import com.zrlog.common.vo.DatabaseConnectPoolInfo;
import com.zrlog.plugin.Plugins;

import java.util.Map;


public abstract class ZrLogConfig extends AbstractServerConfig {
    /**
     * 调用了该方法，主要用于配置，启动插件功能，以及相应的ZrLog的插件服务。
     */
    public abstract void startPluginsAsync();

    public abstract void configDatabase() throws Exception;

    public abstract Plugins getPlugins();

    public abstract Updater getUpdater();

    public abstract CacheService getCacheService();

    public abstract TokenService getTokenService();

    public abstract Map<String, Object> getPublicWebSite();

    public abstract String getProgramUptime();

    public abstract DatabaseConnectPoolInfo getDatabaseConnectPoolInfo();
}
