package com.zrlog.web;

import com.hibegin.common.util.EnvKit;
import com.zrlog.common.Constants;
import com.zrlog.common.Updater;
import com.zrlog.common.adapter.UpdaterHosts;
import com.zrlog.updater.common.UpdaterRuntime;
import com.zrlog.updater.common.UpdaterUtils;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.ZrLogUtil;

import java.io.File;

/**
 * Supplies the application's runtime identity and host adapter to updater-common.
 */
public final class ApplicationUpdaterFactory {
    private ApplicationUpdaterFactory() {
    }

    public static Updater create(String[] args, File executable) {
        UpdaterRuntime runtime = new UpdaterRuntime(ZrLogUtil.isDockerMode(), EnvKit.isFaaSMode(),
                BlogBuildInfoUtil.getPackageType(), EnvKit.isNativeImage(),
                System.getProperty("java.class.path", ""), new File(Constants.getZrLogHome()));
        UpdaterHosts.ApplicationHost host = UpdaterHosts.application(args, executable);
        return host.adapt(UpdaterUtils.create(runtime, host.context()));
    }

    public static Updater createWar(File executable) {
        UpdaterRuntime runtime = UpdaterRuntime.servlet(new File(Constants.getZrLogHome()));
        UpdaterHosts.ApplicationHost host = UpdaterHosts.application(new String[0], executable);
        return host.adapt(UpdaterUtils.create(runtime, host.context()));
    }
}
