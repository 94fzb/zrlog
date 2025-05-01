package com.zrlog.web.setup.install;

import com.hibegin.common.util.EnvKit;
import com.zrlog.common.ZrLogConfig;
import com.zrlog.install.web.config.DefaultInstallAction;

import java.io.File;

public class ZrLogInstallAction extends DefaultInstallAction {

    private final ZrLogConfig zrLogConfig;
    private final File lockFile;

    public ZrLogInstallAction(ZrLogConfig zrLogConfig, File lockFile) {
        this.zrLogConfig = zrLogConfig;
        this.lockFile = lockFile;
    }

    @Override
    public void installSuccess() {
        try {
            zrLogConfig.configDatabase();
            zrLogConfig.startPlugins(!EnvKit.isFaaSMode());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isInstalled() {
        return zrLogConfig.isInstalled();
    }

    @Override
    public File getLockFile() {
        return lockFile;
    }
}
