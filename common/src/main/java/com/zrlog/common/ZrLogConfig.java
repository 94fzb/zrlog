package com.zrlog.common;

import com.hibegin.http.server.config.AbstractServerConfig;
import com.zrlog.plugin.Plugins;
import com.zrlog.util.JarUpdater;


public abstract class ZrLogConfig extends AbstractServerConfig {

    /**
     * 当安装流程正常执行完成时，调用了该方法，主要用于配置，启动插件功能，以及相应的ZrLog的插件服务。
     */
    public abstract void installFinish();

    public abstract Plugins getPlugins();

    public abstract JarUpdater getJarUpdater();

    public abstract CacheService getCacheService();

    public abstract TokenService getTokenService();
}
