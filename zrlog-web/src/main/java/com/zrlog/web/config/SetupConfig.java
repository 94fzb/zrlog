package com.zrlog.web.config;

import com.zrlog.common.Updater;
import com.zrlog.common.ZrLogConfig;
import com.zrlog.web.WebSetup;
import com.zrlog.web.WebSetupContext;
import com.zrlog.web.WebSetupLoader;

import java.io.File;
import java.util.List;

public class SetupConfig {

    public SetupConfig(ZrLogConfig zrLogConfig, File dbPropertiesFile,
                       File installLockFile, String contextPath,
                       List<WebSetup> webSetups, Updater updater) {
        webSetups.addAll(WebSetupLoader.load(new WebSetupContext(
                zrLogConfig, dbPropertiesFile, installLockFile, contextPath, updater)));
    }
}
