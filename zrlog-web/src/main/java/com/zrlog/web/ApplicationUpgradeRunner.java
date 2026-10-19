package com.zrlog.web;

import com.zrlog.business.di.ServiceComponent;
import com.zrlog.business.rest.response.UpgradeProcessResponse;
import com.zrlog.business.updater.UpdateVersionTimerTask;
import com.zrlog.common.Updater;
import com.zrlog.common.adapter.UpdaterHosts;
import com.zrlog.common.vo.Version;
import com.zrlog.updater.spi.ApplicationUpdater;
import com.zrlog.updater.spi.SystemPackageUpdater;
import com.zrlog.util.I18nUtil;

import java.util.Map;
import java.util.Objects;

final class ApplicationUpgradeRunner {

    private ApplicationUpgradeRunner() {
    }

    static int run(Updater updater, boolean preview) {
        return run(updater, preview, false);
    }

    static int run(Updater updater, boolean preview, boolean json) {
        if (updater == null) {
            System.err.println("The current package does not provide an updater");
            return 1;
        }
        ApplicationUpdater implementation = UpdaterHosts.unwrap(updater);
        if (implementation instanceof SystemPackageUpdater) {
            try {
                return ((SystemPackageUpdater) implementation)
                        .install(preview ? "preview" : "release", json);
            } catch (Exception e) {
                System.err.println("deb update failed: " + e.getMessage()); return 1; }
        }
        UpdateVersionTimerTask versionTask = new UpdateVersionTimerTask(preview, I18nUtil.getCurrentLocale());
        versionTask.run();
        Version version = versionTask.getVersion();
        if (version == null) {
            System.err.println("Unable to fetch the ZrLog update manifest");
            return 1;
        }
        Map<String, Object> backend = I18nUtil.getBackend();
        UpgradeProcessResponse response =
                ServiceComponent.forUninstalledApplication(updater)
                        .upgrades()
                        .doUpgrade(
                                version,
                                updater,
                                (event, data) -> {
                                    String detail =
                                            Objects.nonNull(data.getDetail())
                                                    ? " " + data.getDetail()
                                                    : "";
                                    System.out.println(
                                            "["
                                                    + data.getStage()
                                                    + "/"
                                                    + data.getStatus()
                                                    + "] "
                                                    + data.getMessage()
                                                    + detail);
                                },
                                backend);
        System.out.println(response.getMessage());
        return Boolean.TRUE.equals(response.getFinish()) ? 0 : 1;
    }
}
