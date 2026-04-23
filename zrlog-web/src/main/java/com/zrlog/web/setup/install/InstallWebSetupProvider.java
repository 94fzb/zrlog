package com.zrlog.web.setup.install;

import com.zrlog.web.WebSetup;
import com.zrlog.web.WebSetupContext;
import com.zrlog.web.WebSetupProvider;

public class InstallWebSetupProvider implements WebSetupProvider {

    @Override
    public String name() {
        return "install";
    }

    @Override
    public int order() {
        return 200;
    }

    @Override
    public WebSetup create(WebSetupContext context) {
        return new InstallWebSetup(context.getZrLogConfig(), context.getDbPropertiesFile(),
                context.getInstallLockFile(), context.getUpdater());
    }
}
