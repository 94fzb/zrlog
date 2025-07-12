package com.zrlog.web.config;

import com.zrlog.blog.web.plugin.TemplateDownloadPlugin;
import com.zrlog.business.plugin.PluginCorePlugin;
import com.zrlog.business.plugin.StaticSitePlugin;
import com.zrlog.common.Constants;
import com.zrlog.plugin.IPlugin;

import java.util.Objects;

public class PluginUtils {

    public static void refreshPluginCacheData(boolean includeBlog, long cacheVersion) {
        if (includeBlog) {
            //下载主题
            TemplateDownloadPlugin templatePlugin = Constants.zrLogConfig.getPlugin(TemplateDownloadPlugin.class);
            if (Objects.nonNull(templatePlugin)) {
                templatePlugin.stop();
                templatePlugin.start();
            }
        }

        //启动插件
        PluginCorePlugin pluginCorePlugin = Constants.zrLogConfig.getPlugin(PluginCorePlugin.class);
        if (Objects.nonNull(pluginCorePlugin) && !pluginCorePlugin.isStarted()) {
            pluginCorePlugin.start();
        }
        if (includeBlog) {
            //静态化插件，重新生成全量的 html
            StaticSitePlugin staticSitePlugin = Constants.zrLogConfig.getPlugin(StaticSitePlugin.class);
            if (Objects.nonNull(staticSitePlugin)) {
                //restart plugin, for update
                staticSitePlugin.stop();
                staticSitePlugin.setVersion(cacheVersion);
                staticSitePlugin.start();
            }
        }
        //plugin cache
        if (Objects.nonNull(pluginCorePlugin)) {
            pluginCorePlugin.refreshCache(cacheVersion);
        }
    }

    public static void stopAllPlugin() {
        Constants.zrLogConfig.getPlugins().forEach(IPlugin::stop);
        Constants.zrLogConfig.getPlugins().clear();
    }
}
