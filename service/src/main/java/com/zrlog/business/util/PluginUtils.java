package com.zrlog.business.util;

import com.zrlog.business.plugin.StaticSitePlugin;
import com.zrlog.business.plugin.TemplateDownloadPlugin;
import com.zrlog.common.Constants;
import com.zrlog.common.PluginCorePlugin;
import com.zrlog.plugin.IPlugin;

import java.util.Optional;

public class PluginUtils {

    public static void refreshPluginCacheData() {
        //下载主题
        Optional<IPlugin> templatePlugin = Constants.zrLogConfig.getPlugins().stream().filter(e -> e instanceof TemplateDownloadPlugin).findFirst();
        templatePlugin.ifPresent((e) -> {
            e.stop();
            e.start();
        });
        //启动插件
        Optional<IPlugin> corePlugin = Constants.zrLogConfig.getPlugins().stream().filter(e -> e instanceof PluginCorePlugin).findFirst();
        if (corePlugin.isPresent() && !corePlugin.get().isStarted()) {
            corePlugin.ifPresent(IPlugin::start);
        }
        //静态化插件，重新生成全量的 html
        Optional<IPlugin> stateSitePluginOp = Constants.zrLogConfig.getPlugins().stream().filter(e -> e instanceof StaticSitePlugin).findFirst();
        if (stateSitePluginOp.isPresent() && stateSitePluginOp.get() instanceof StaticSitePlugin staticSitePlugin) {
            //restart plugin, for update
            staticSitePlugin.stop();
            staticSitePlugin.start();
        }
        //plugin cache
        if (corePlugin.isPresent() && corePlugin.get() instanceof PluginCorePlugin pc) {
            pc.refreshCache();
        }
    }
}
