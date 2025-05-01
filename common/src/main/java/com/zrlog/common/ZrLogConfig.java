package com.zrlog.common;

import com.hibegin.common.util.LoggerUtil;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.config.AbstractServerConfig;
import com.zrlog.plugin.IPlugin;
import com.zrlog.plugin.Plugins;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;


public abstract class ZrLogConfig extends AbstractServerConfig {

    protected static final Logger LOGGER = LoggerUtil.getLogger(ZrLogConfig.class);


    static {
        disableHikariLogging();
    }

    private static void disableHikariLogging() {
        System.setProperty("org.slf4j.simpleLogger.log.com.zaxxer.hikari", "off");
    }

    private final Map<String, Map<String, Object>> templateConfigCacheMap = new ConcurrentHashMap<>();


    public boolean isTest() {
        return "junit-test".equals(System.getProperties().getProperty("env"));
    }

    /**
     * 调用了该方法，主要用于配置，启动插件功能，以及相应的ZrLog的插件服务。
     */
    public abstract void startPluginsAsync();

    public abstract void configDatabase() throws Exception;

    public abstract Plugins getPlugins();

    public <T extends IPlugin> T getPlugin(Class<T> pluginClass) {
        for (IPlugin plugin : getPlugins()) {
            if (pluginClass.isInstance(plugin)) {
                return pluginClass.cast(plugin);
            }
        }
        return null;
    }

    public abstract Updater getUpdater();

    public abstract CacheService<?> getCacheService();

    public abstract TokenService getTokenService();

    public abstract Map<String, Object> getPublicWebSite();

    public abstract String getProgramUptime();


    public abstract DataSource getDataSource();

    public abstract void stop();

    public abstract AdminResource getAdminResource();

    public Map<String, Map<String, Object>> getTemplateConfigCacheMap() {
        return templateConfigCacheMap;
    }

    public List<String> getStaticResourcePath() {
        return Arrays.asList("/assets", "/admin/static", "/admin/vendors", "/install/static");
    }

    public abstract void refreshPluginCacheData(long version);

    public abstract boolean isStaticPluginRequest(HttpRequest request);

    /**
     * 通过检查特定目录下面是否存在 install.lock 文件，同时判断环境变量里面是否存在配置，进行判断是否已经完成安装
     */
    public abstract boolean isInstalled();
}
