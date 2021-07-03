package com.zrlog.admin.web.controller.api;

import com.jfinal.core.Controller;
import com.zrlog.admin.business.rest.response.CheckVersionResponse;
import com.zrlog.admin.business.rest.response.DownloadUpdatePackageResponse;
import com.zrlog.admin.business.rest.response.UpgradeProcessResponse;
import com.zrlog.admin.business.service.UpgradeService;

import java.io.IOException;

public class UpgradeController extends Controller {

    private final UpgradeService upgradeService;

    public UpgradeController() {
        this.upgradeService = new UpgradeService();
    }

    public DownloadUpdatePackageResponse download() throws IOException {
        return upgradeService.download();
    }

    public CheckVersionResponse lastVersion() {
        return upgradeService.getCheckVersionResponse(false);
    }

    public CheckVersionResponse checkNewVersion() {
        return upgradeService.getCheckVersionResponse(true);
    }

    public UpgradeProcessResponse doUpgrade() {
        return upgradeService.doUpgrade();
    }

}
