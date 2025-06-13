package com.zrlog.common;

import com.hibegin.http.server.util.PathUtil;
import com.zrlog.common.vo.Version;

import java.io.File;

public interface Updater {

    void restartProcessAsync(Version upgradeVersion);

    default File getUpdateTempPath() {
        return PathUtil.getConfFile("/update-temp");
    }

    default String buildUpgradeFile() {
        return null;
    }

    default void backup() {
    }

    String getUnzipPath();

    File execFile();
}
