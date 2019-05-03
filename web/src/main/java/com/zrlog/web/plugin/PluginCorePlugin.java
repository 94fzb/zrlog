package com.zrlog.web.plugin;

import com.jfinal.kit.PathKit;
import com.jfinal.plugin.IPlugin;
import com.zrlog.util.BlogBuildInfoUtil;

import java.io.File;

/**
 * 运行Zrlog的插件，当 WEB-INF/plugins/这里目录下面不存在plugin-core.jar时，会通过网络请求下载最新的plugin核心服务，也可以通过
 * 这种方式进行插件的及时更新。
 * plugin-core也是一个java进程，通过调用系统命令的命令进行启动的。
 */
public class PluginCorePlugin implements IPlugin {

    private String dbPropertiesPath;
    private String pluginJvmArgs;

    public PluginCorePlugin(String dbPropertiesPath, String pluginJvmArgs) {
        this.dbPropertiesPath = dbPropertiesPath;
        this.pluginJvmArgs = pluginJvmArgs;
    }

    @Override
    public boolean start() {
        //加载 zrlog 提供的插件
        int port = PluginCoreProcess.getInstance().pluginServerStart(new File(PathKit.getWebRootPath() + "/WEB-INF/plugins/plugin-core.jar"),
                dbPropertiesPath, pluginJvmArgs, PathKit.getWebRootPath(), BlogBuildInfoUtil.getVersion());
        com.zrlog.common.Constants.pluginServer = "http://127.0.0.1:" + port;
        return true;
    }

    @Override
    public boolean stop() {
        PluginCoreProcess.getInstance().stopPluginCore();
        return true;
    }
}
