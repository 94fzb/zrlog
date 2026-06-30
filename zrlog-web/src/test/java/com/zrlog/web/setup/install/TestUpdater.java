package com.zrlog.web.setup.install;

import com.zrlog.common.Updater;
import com.zrlog.common.UpdaterTypeEnum;
import com.zrlog.common.vo.Version;

import java.io.File;

class TestUpdater implements Updater {

    private final UpdaterTypeEnum type;

    TestUpdater(UpdaterTypeEnum type) {
        this.type = type;
    }

    @Override
    public void restartProcessAsync(Version upgradeVersion) {
    }

    @Override
    public String getUnzipPath() {
        return "";
    }

    @Override
    public File execFile() {
        return null;
    }

    @Override
    public UpdaterTypeEnum getType() {
        return type;
    }
}
