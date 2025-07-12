package com.zrlog.admin.web.controller.api;

import com.hibegin.http.annotation.ResponseBody;
import com.zrlog.admin.business.rest.response.DownloadUpdatePackageResponse;
import com.zrlog.admin.business.rest.response.PreCheckVersionResponse;
import com.zrlog.admin.business.rest.response.UpgradeProcessResponse;
import com.zrlog.admin.business.service.AdminStaticService;
import com.zrlog.admin.web.plugin.UpdateVersionInfoPlugin;
import com.zrlog.common.Constants;
import com.zrlog.common.controller.BaseController;
import com.zrlog.common.rest.response.ApiStandardResponse;

import java.text.ParseException;

public class UpgradeController extends BaseController {


    @ResponseBody
    public ApiStandardResponse<DownloadUpdatePackageResponse> download() {
        return new ApiStandardResponse<>(AdminStaticService.getInstance().getUpgradeService().download(getParamWithEmptyCheck("preUpgradeKey"), Constants.zrLogConfig.getPlugin(UpdateVersionInfoPlugin.class)));
    }

    @ResponseBody
    public ApiStandardResponse<PreCheckVersionResponse> index() throws ParseException {
        return new ApiStandardResponse<>(AdminStaticService.getInstance().getUpgradeService().preUpgradeVersion(true, Constants.zrLogConfig.getPlugin(UpdateVersionInfoPlugin.class), System.currentTimeMillis() + ""));
    }


    @ResponseBody
    public ApiStandardResponse<UpgradeProcessResponse> doUpgrade() {
        return new ApiStandardResponse<>(AdminStaticService.getInstance().getUpgradeService().doUpgrade(getParamWithEmptyCheck("preUpgradeKey")));
    }

}
