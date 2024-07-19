package com.zrlog.admin.web.controller.api;

import com.hibegin.http.annotation.ResponseBody;
import com.hibegin.http.server.web.Controller;
import com.zrlog.admin.business.rest.response.DownloadUpdatePackageResponse;
import com.zrlog.admin.business.rest.response.PreCheckVersionResponse;
import com.zrlog.admin.business.rest.response.UpgradeProcessResponse;
import com.zrlog.admin.business.service.UpgradeService;
import com.zrlog.admin.web.plugin.UpdateVersionPlugin;
import com.zrlog.common.Constants;
import com.zrlog.common.rest.response.ApiStandardResponse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Objects;

public class UpgradeController extends Controller {

    private final UpgradeService upgradeService = new UpgradeService();

    @ResponseBody
    public ApiStandardResponse<DownloadUpdatePackageResponse> download() throws IOException, URISyntaxException, InterruptedException, ParseException {
        return new ApiStandardResponse<>(upgradeService.download(Objects.requireNonNullElse(request.getParaToStr("preUpgradeKey"),""), (UpdateVersionPlugin) Constants.zrLogConfig.getPlugins()
                .stream().filter(x -> x instanceof UpdateVersionPlugin).findFirst().orElse(null)));
    }

    @ResponseBody
    public ApiStandardResponse<PreCheckVersionResponse> index() throws ParseException {
        return new ApiStandardResponse<>(upgradeService.preUpgradeVersion(true, (UpdateVersionPlugin) Constants.zrLogConfig.getPlugins()
                .stream().filter(x -> x instanceof UpdateVersionPlugin).findFirst().orElse(null), System.currentTimeMillis() + ""));
    }


    @ResponseBody
    public ApiStandardResponse<UpgradeProcessResponse> doUpgrade() {
        return new ApiStandardResponse<>(upgradeService.doUpgrade(Objects.requireNonNullElse(request.getParaToStr("preUpgradeKey"), "")));
    }

}
