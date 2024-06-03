package com.zrlog.common;

import com.hibegin.http.server.util.PathUtil;
import com.zrlog.common.vo.Version;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public interface Updater {

    CompletableFuture<Void> restartProcessAsync(Version upgradeVersion);

    String fileName();

    default File getUploadTempPath() {
        return PathUtil.getConfFile("/update-temp");
    }

    String getUnzipPath();
}
