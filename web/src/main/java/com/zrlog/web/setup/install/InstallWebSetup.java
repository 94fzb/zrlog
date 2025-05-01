package com.zrlog.web.setup.install;

import com.zrlog.common.ZrLogConfig;
import com.zrlog.install.web.InstallConstants;
import com.zrlog.install.web.config.InstallRouters;
import com.zrlog.install.web.interceptor.BlogInstallInterceptor;
import com.zrlog.web.WebSetup;

import java.io.File;

public class InstallWebSetup implements WebSetup {

    private final ZrLogConfig zrLogConfig;
    private final File dbPropertiesFile;
    private final File lockFile;

    public InstallWebSetup(ZrLogConfig zrLogConfig, File dbPropertiesFile, File lockFile) {
        this.zrLogConfig = zrLogConfig;
        this.dbPropertiesFile = dbPropertiesFile;
        this.lockFile = lockFile;
    }

    @Override
    public void setup() {
        InstallConstants.installConfig = new ZrLogInstallConfig(zrLogConfig, dbPropertiesFile, lockFile);
        InstallRouters.configRouter(zrLogConfig.getServerConfig());
        zrLogConfig.getServerConfig().addInterceptor(BlogInstallInterceptor.class);
    }
}
