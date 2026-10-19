package com.zrlog.web.setup.install;

import com.hibegin.common.util.LoggerUtil;
import com.hibegin.http.server.config.ServerConfig;
import com.zrlog.common.Updater;
import com.zrlog.common.ZrLogConfig;
import com.zrlog.install.util.InstallNativeImageResourceUtils;
import com.zrlog.install.web.InstallAction;
import com.zrlog.install.web.config.InstallWebBindings;
import com.zrlog.web.WebSetup;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InstallWebSetup implements WebSetup {

    private static final Logger LOGGER = LoggerUtil.getLogger(InstallWebSetup.class);
    private final ZrLogConfig zrLogConfig;
    private final File dbPropertiesFile;
    private final File lockFile;
    private final Updater updater;

    public InstallWebSetup(ZrLogConfig zrLogConfig, File dbPropertiesFile, File lockFile, Updater updater) {
        this.zrLogConfig = zrLogConfig;
        this.dbPropertiesFile = dbPropertiesFile;
        this.lockFile = lockFile;
        if (zrLogConfig.getServerConfig().isNativeImageAgent()) {
            InstallNativeImageResourceUtils.reg();
        }
        this.updater = updater;
    }

    @Override
    public void setup() {
        ZrLogInstallConfig installConfig = new ZrLogInstallConfig(zrLogConfig, dbPropertiesFile, lockFile, updater);
        ServerConfig serverConfig = zrLogConfig.getServerConfig();
        InstallWebBindings.configure(serverConfig, installConfig);
        InstallAction action = installConfig.getAction();
        if (action.isInstalled()) {
            return;
        }
        LOGGER.log(Level.WARNING, "Installation is not complete. Visit http://your-host:port"
                + zrLogConfig.getServerConfig().getContextPath()
                + "/install to start installation.");
    }

}
