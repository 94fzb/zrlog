package com.zrlog.admin.web.plugin;

import com.hibegin.common.util.LoggerUtil;
import com.hibegin.common.util.http.HttpUtil;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.common.Constants;
import com.zrlog.common.PluginCorePlugin;
import com.zrlog.common.PluginCoreProcess;
import com.zrlog.common.type.RunMode;
import com.zrlog.util.BlogBuildInfoUtil;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.logging.Level;

/**
 * 运行ZrLog的插件，当 conf/plugins/这里目录下面不存在插件核心服务时，会通过网络请求下载最新的插件核心服务，也可以通过
 * 这种方式进行插件的及时更新。
 * 插件核心服务通过调用系统命令的命令进行启动的。
 */
public class PluginCorePluginImpl implements PluginCorePlugin {

    private final File dbPropertiesPath;
    private final String pluginJvmArgs;
    private final PluginCoreProcess pluginCoreProcess;
    private final String token;

    public PluginCorePluginImpl(File dbPropertiesPath, String pluginJvmArgs, PluginCoreProcess pluginCoreProcess, String token) {
        this.dbPropertiesPath = dbPropertiesPath;
        this.pluginJvmArgs = pluginJvmArgs;
        this.pluginCoreProcess = pluginCoreProcess;
        this.token = token;
    }

    private String getPluginFileName() {
        if (Constants.runMode == RunMode.NATIVE) {
            if (Constants.getRealFileArch().contains("Window")) {
                return "plugin-core-" + Constants.getRealFileArch() + ".exe";
            }
            return "plugin-core-" + Constants.getRealFileArch() + ".bin";
        }
        return "plugin-core.jar";
    }

    @Override
    public boolean start() {
        if (Objects.nonNull(Constants.pluginServer)) {
            return true;
        }
        //加载 ZrLog 提供的插件
        int port = pluginCoreProcess.pluginServerStart(new File(PathUtil.getConfPath() + "/plugins/" + getPluginFileName()),
                dbPropertiesPath.toString(), pluginJvmArgs, PathUtil.getStaticPath(), BlogBuildInfoUtil.getVersion(), token);
        Constants.pluginServer = "http://127.0.0.1:" + port;
        return true;
    }

    @Override
    public boolean isStarted() {
        return Objects.nonNull(Constants.pluginServer);
    }

    public String getToken() {
        return token;
    }

    @Override
    public boolean stop() {
        pluginCoreProcess.stopPluginCore();
        Constants.pluginServer = null;
        return true;
    }

    @Override
    public void refreshCache() {
        if (Objects.isNull(Constants.pluginServer)) {
            return;
        }
        refreshCacheWithRetry(5);
    }

    private void refreshCacheWithRetry(int retryCount) {
        try {
            HttpUtil.getInstance().getSuccessTextByUrl(Constants.pluginServer + "/api/refreshCache?token=" + token);
        } catch (IOException | InterruptedException | URISyntaxException e) {
            int rCount = retryCount - 1;
            if (retryCount < 0) {
                LoggerUtil.getLogger(PluginCorePluginImpl.class).log(Level.SEVERE, "refresh plugin cache error ", e);
                return;
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            refreshCacheWithRetry(rCount);
        }
    }
}
