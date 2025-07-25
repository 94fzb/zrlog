package com.zrlog.web.config;

import com.hibegin.http.server.api.HttpRequest;
import com.zrlog.blog.web.plugin.TemplateDownloadPlugin;
import com.zrlog.business.plugin.PluginCorePlugin;
import com.zrlog.business.plugin.StaticSitePlugin;
import com.zrlog.common.Constants;

import java.util.Objects;

public class PluginUtils {

    public static void refreshPluginCacheData(String cacheVersion, HttpRequest request) {
        //下载主题
        TemplateDownloadPlugin templatePlugin = Constants.zrLogConfig.getPlugin(TemplateDownloadPlugin.class);
        if (Objects.nonNull(templatePlugin)) {
            templatePlugin.stop();
            templatePlugin.start();
        }
        //启动插件
        PluginCorePlugin pluginCorePlugin = Constants.zrLogConfig.getPlugin(PluginCorePlugin.class);
        if (Objects.nonNull(pluginCorePlugin) && !pluginCorePlugin.isStarted()) {
            pluginCorePlugin.start();
        }
        if (!StaticSitePlugin.isDisabled()) {
            //静态化插件，重新生成全量的 html
            Constants.zrLogConfig.getPluginsByClazz(StaticSitePlugin.class).forEach(e -> {
                if (Objects.nonNull(e)) {
                    //restart plugin, for update
                    e.stop();
                    e.start();
                }
            });
        }
        //plugin cache
        if (Objects.nonNull(pluginCorePlugin)) {
            pluginCorePlugin.refreshCache(cacheVersion, request);
        }
    }
}
