package com.zrlog.web.setup.install;

import com.hibegin.common.util.LoggerUtil;
import com.hibegin.http.server.util.NativeImageUtils;
import com.zrlog.common.Updater;
import com.zrlog.common.ZrLogConfig;
import com.zrlog.install.util.InstallNativeImageResourceUtils;
import com.zrlog.install.web.InstallAction;
import com.zrlog.install.web.InstallConstants;
import com.zrlog.install.web.config.InstallRouters;
import com.zrlog.install.web.interceptor.BlogInstallInterceptor;
import com.zrlog.web.WebSetup;

import java.io.File;
import java.util.Arrays;
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
        InstallConstants.installConfig = new ZrLogInstallConfig(zrLogConfig, dbPropertiesFile, lockFile, updater);
        InstallRouters.configRouter(zrLogConfig.getServerConfig());
        zrLogConfig.getServerConfig().addInterceptor(BlogInstallInterceptor.class);
        InstallAction action = InstallConstants.installConfig.getAction();
        if (!action.isInstalled()) {
            LOGGER.log(Level.WARNING, "Not found lock file(" + action.getLockFile() + "), Please visit the http://yourHostName:port" + zrLogConfig.getServerConfig().getContextPath() + "/install start installation");
        }
    }
}
