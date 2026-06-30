package com.zrlog.web.setup.install;

import com.hibegin.common.dao.DataSourceWrapper;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.common.TokenService;
import com.zrlog.common.Updater;
import com.zrlog.common.ZrLogConfig;
import com.zrlog.plugin.IPlugin;
import com.zrlog.plugin.Plugins;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

class TestZrLogConfig extends ZrLogConfig {

    private boolean installed;
    private boolean askConfig;
    private boolean missingConfig;
    private boolean failConfigDatabase;
    private boolean trackLifecycleCalls;
    private int configDatabaseCalls;
    private int startPluginsCalls;
    private Boolean lastStartPluginsAsync;

    private TestZrLogConfig(Updater updater) {
        super(18080, updater, "/");
    }

    static TestZrLogConfig installedConfig(Updater updater) throws IOException {
        File rootPath = Files.createTempDirectory("zrlog-install-test").toFile();
        rootPath.deleteOnExit();
        PathUtil.setRootPath(rootPath.getAbsolutePath());
        TestZrLogConfig config = new TestZrLogConfig(updater);
        config.installed = true;
        config.trackLifecycleCalls = true;
        return config;
    }

    @Override
    public DataSourceWrapper configDatabase() throws Exception {
        if (failConfigDatabase) {
            throw new IllegalStateException("database unavailable");
        }
        if (trackLifecycleCalls) {
            configDatabaseCalls++;
        }
        return null;
    }

    @Override
    public void startPlugins(boolean async) {
        if (trackLifecycleCalls) {
            startPluginsCalls++;
            lastStartPluginsAsync = async;
        }
    }

    @Override
    public boolean isInstalled() {
        return installed;
    }

    void setAskConfig(boolean askConfig) {
        this.askConfig = askConfig;
    }

    @Override
    public boolean isAskConfig() {
        return askConfig;
    }

    void setMissingConfig(boolean missingConfig) {
        this.missingConfig = missingConfig;
    }

    @Override
    public boolean isMissingConfig() {
        return missingConfig;
    }

    void setFailConfigDatabase(boolean failConfigDatabase) {
        this.failConfigDatabase = failConfigDatabase;
    }

    int getConfigDatabaseCalls() {
        return configDatabaseCalls;
    }

    int getStartPluginsCalls() {
        return startPluginsCalls;
    }

    Boolean getLastStartPluginsAsync() {
        return lastStartPluginsAsync;
    }

    @Override
    protected TokenService initTokenService() {
        return null;
    }

    @Override
    public List<IPlugin> getBasePluginList() {
        return new Plugins();
    }
}
