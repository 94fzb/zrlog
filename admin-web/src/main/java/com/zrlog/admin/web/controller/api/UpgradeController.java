package com.zrlog.admin.web.controller.api;

import com.hibegin.http.annotation.ResponseBody;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
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

    private final UpgradeService upgradeService;

    public UpgradeController() {
        this.upgradeService = new UpgradeService();
    }


    public UpgradeController(HttpRequest request, HttpResponse response) {
        super(request, response);
        this.upgradeService = new UpgradeService();
    }

    @ResponseBody
    public ApiStandardResponse<DownloadUpdatePackageResponse> download() throws IOException, URISyntaxException, InterruptedException, ParseException {
        return new ApiStandardResponse<>(upgradeService.download(request.getParaToStr("preUpgradeKey"), (UpdateVersionPlugin) Objects.requireNonNull(Constants.zrLogConfig.getPlugins()
                .stream().filter(x -> x instanceof UpdateVersionPlugin).findFirst().orElse(null))));
    }

    @ResponseBody
    public ApiStandardResponse<PreCheckVersionResponse> index() throws ParseException {
        return new ApiStandardResponse<>(upgradeService.preUpgradeVersion(true, (UpdateVersionPlugin) Objects.requireNonNull(Constants.zrLogConfig.getPlugins()
                .stream().filter(x -> x instanceof UpdateVersionPlugin).findFirst().orElse(null)),System.currentTimeMillis() + ""));
    }


    @ResponseBody
    public ApiStandardResponse<UpgradeProcessResponse> doUpgrade() {
        return new ApiStandardResponse<>(upgradeService.doUpgrade(request.getParaToStr("preUpgradeKey")));
    }

}
