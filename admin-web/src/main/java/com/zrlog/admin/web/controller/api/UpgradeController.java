package com.zrlog.admin.web.controller.api;

import com.hibegin.http.annotation.ResponseBody;
import com.hibegin.http.server.web.Controller;
import com.zrlog.admin.business.service.UpgradeService;
import com.zrlog.admin.web.plugin.UpdateVersionPlugin;
import com.zrlog.common.Constants;
import com.zrlog.common.rest.response.ApiStandardResponse;

import java.io.IOException;
import java.util.Objects;

public class UpgradeController extends Controller {

    private final UpgradeService upgradeService;

    public UpgradeController() {
        this.upgradeService = new UpgradeService();
    }

    @ResponseBody
    public ApiStandardResponse download() throws IOException {
        return new ApiStandardResponse(upgradeService.download((UpdateVersionPlugin) Constants.plugins
                .stream().filter(x -> x instanceof UpdateVersionPlugin).findFirst().orElse(null)));
    }

    @ResponseBody
    public ApiStandardResponse lastVersion() {
        return new ApiStandardResponse(upgradeService.getCheckVersionResponse(false, (UpdateVersionPlugin) Objects.requireNonNull(Constants.plugins
                .stream().filter(x -> x instanceof UpdateVersionPlugin).findFirst().orElse(null))));
    }

    @ResponseBody
    public ApiStandardResponse checkNewVersion() {
        return new ApiStandardResponse(upgradeService.getCheckVersionResponse(true, (UpdateVersionPlugin) Objects.requireNonNull(Constants.plugins
                .stream().filter(x -> x instanceof UpdateVersionPlugin).findFirst().orElse(null))));
    }

    @ResponseBody
    public ApiStandardResponse doUpgrade() {
        return new ApiStandardResponse(upgradeService.doUpgrade());
    }

}
