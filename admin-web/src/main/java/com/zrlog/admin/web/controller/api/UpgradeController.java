package com.zrlog.admin.web.controller.api;

import com.hibegin.http.annotation.ResponseBody;
import com.hibegin.http.server.web.Controller;
import com.zrlog.admin.business.rest.response.DownloadUpdatePackageResponse;
import com.zrlog.admin.business.rest.response.PreCheckVersionResponse;
import com.zrlog.admin.business.rest.response.UpgradeProcessResponse;
import com.zrlog.admin.business.service.UpgradeService;
import com.zrlog.business.plugin.UpdateVersionInfoPlugin;
import com.zrlog.common.Constants;
import com.zrlog.common.rest.response.ApiStandardResponse;

import java.text.ParseException;

public class UpgradeController extends Controller {

    private final UpgradeService upgradeService = new UpgradeService();

    @ResponseBody
    public ApiStandardResponse<DownloadUpdatePackageResponse> download() {
        return new ApiStandardResponse<>(upgradeService.download(request.getParaToStr("preUpgradeKey", ""), (UpdateVersionInfoPlugin) Constants.zrLogConfig.getPlugins()
                .stream().filter(x -> x instanceof UpdateVersionInfoPlugin).findFirst().orElse(null)));
    }

    @ResponseBody
    public ApiStandardResponse<PreCheckVersionResponse> index() throws ParseException {
        return new ApiStandardResponse<>(upgradeService.preUpgradeVersion(true, (UpdateVersionInfoPlugin) Constants.zrLogConfig.getPlugins()
                .stream().filter(x -> x instanceof UpdateVersionInfoPlugin).findFirst().orElse(null), System.currentTimeMillis() + ""));
    }


    @ResponseBody
    public ApiStandardResponse<UpgradeProcessResponse> doUpgrade() {
        return new ApiStandardResponse<>(upgradeService.doUpgrade(request.getParaToStr("preUpgradeKey", "")));
    }

}
