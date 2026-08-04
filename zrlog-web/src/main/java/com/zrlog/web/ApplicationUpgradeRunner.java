package com.zrlog.web;

import com.zrlog.business.rest.response.UpgradeProcessResponse;
import com.zrlog.business.service.UpgradeService;
import com.zrlog.common.Updater;
import com.zrlog.common.updater.UpdateVersionTimerTask;
import com.zrlog.common.vo.Version;
import com.zrlog.util.I18nUtil;

import java.util.Map;
import java.util.Objects;

final class ApplicationUpgradeRunner {

    private ApplicationUpgradeRunner() {
    }

    static int run(Updater updater, boolean preview) {
        if (updater == null) {
            System.err.println("The current package does not provide an updater");
            return 1;
        }
        UpdateVersionTimerTask versionTask = new UpdateVersionTimerTask(preview, I18nUtil.getCurrentLocale());
        versionTask.run();
        Version version = versionTask.getVersion();
        if (version == null) {
            System.err.println("Unable to fetch the ZrLog update manifest");
            return 1;
        }
        Map<String, Object> backend = I18nUtil.getBackend();
        UpgradeProcessResponse response = new UpgradeService().doUpgrade(version, updater, (event, data) -> {
            String detail = Objects.nonNull(data.getDetail()) ? " " + data.getDetail() : "";
            System.out.println("[" + data.getStage() + "/" + data.getStatus() + "] " + data.getMessage() + detail);
        }, backend);
        System.out.println(response.getMessage());
        return Boolean.TRUE.equals(response.getFinish()) ? 0 : 1;
    }
}
