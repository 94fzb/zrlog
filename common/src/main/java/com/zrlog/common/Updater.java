package com.zrlog.common;

import com.hibegin.http.server.util.PathUtil;
import com.zrlog.common.vo.Version;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public interface Updater {

    void restartProcessAsync(Version upgradeVersion);

    File execFile();

    default File getUpdateTempPath() {
        return PathUtil.getConfFile("/update-temp");
    }

    String getUnzipPath();
}
