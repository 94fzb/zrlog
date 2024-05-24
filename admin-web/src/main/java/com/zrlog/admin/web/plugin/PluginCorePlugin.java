package com.zrlog.admin.web.plugin;

import com.hibegin.http.server.util.PathUtil;
import com.zrlog.common.Constants;
import com.zrlog.common.PluginCoreProcess;
import com.zrlog.plugin.IPlugin;
import com.zrlog.util.BlogBuildInfoUtil;

import java.io.File;

/**
 * 运行ZrLog的插件，当 conf/plugins/这里目录下面不存在plugin-core.jar时，会通过网络请求下载最新的plugin核心服务，也可以通过
 * 这种方式进行插件的及时更新。
 * plugin-core也是一个java进程，通过调用系统命令的命令进行启动的。
 */
public class PluginCorePlugin implements IPlugin {

    private final File dbPropertiesPath;
    private final String pluginJvmArgs;
    private final PluginCoreProcess pluginCoreProcess;
    private final String token;

    public PluginCorePlugin(File dbPropertiesPath, String pluginJvmArgs, PluginCoreProcess pluginCoreProcess, String token) {
        this.dbPropertiesPath = dbPropertiesPath;
        this.pluginJvmArgs = pluginJvmArgs;
        this.pluginCoreProcess = pluginCoreProcess;
        this.token = token;
    }

    @Override
    public boolean start() {
        //加载 ZrLog 提供的插件
        int port = pluginCoreProcess.pluginServerStart(new File(PathUtil.getConfPath() + "/plugins/plugin-core.jar"),
                dbPropertiesPath.toString(), pluginJvmArgs, PathUtil.getStaticPath(), BlogBuildInfoUtil.getVersion(),token);
        Constants.pluginServer = "http://127.0.0.1:" + port;
        return true;
    }

    public String getToken() {
        return token;
    }

    @Override
    public boolean stop() {
        pluginCoreProcess.stopPluginCore();
        return true;
    }
}
