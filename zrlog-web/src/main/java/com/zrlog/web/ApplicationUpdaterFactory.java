package com.zrlog.web;

import com.hibegin.common.util.EnvKit;
import com.zrlog.common.Constants;
import com.zrlog.common.Updater;
import com.zrlog.common.UpdaterTypeEnum;
import com.zrlog.common.updater.UpdaterHosts;
import com.zrlog.updater.common.UpdaterUtils;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.ZrLogUtil;

import java.io.File;

/** Supplies the application's runtime identity and host adapter to updater-common. */
public final class ApplicationUpdaterFactory {
    private ApplicationUpdaterFactory() { }

    public static Updater create(String[] args, File executable) {
        UpdaterTypeEnum type = UpdaterUtils.selectType(ZrLogUtil.isDockerMode(), EnvKit.isFaaSMode(),
                BlogBuildInfoUtil.getPackageType(), EnvKit.isNativeImage());
        return UpdaterUtils.getUpdater(type, executable, System.getProperty("java.class.path", ""),
                new File(Constants.getZrLogHome()),
                (selectedType, target) -> UpdaterHosts.create(selectedType, args, target));
    }
}
