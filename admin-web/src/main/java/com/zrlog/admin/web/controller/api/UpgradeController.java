package com.zrlog.admin.web.controller.api;

import com.hibegin.http.annotation.ResponseBody;
import com.zrlog.admin.business.rest.response.AdminApiPageDataStandardResponse;
import com.zrlog.admin.business.rest.response.DownloadUpdatePackageResponse;
import com.zrlog.admin.business.rest.response.PreCheckVersionResponse;
import com.zrlog.admin.business.rest.response.UpgradeProcessResponse;
import com.zrlog.admin.business.service.AdminStaticService;
import com.zrlog.admin.web.plugin.UpdateVersionInfoPlugin;
import com.zrlog.common.Constants;
import com.zrlog.common.controller.BaseController;

import java.text.ParseException;

public class UpgradeController extends BaseController {


    @ResponseBody
    public AdminApiPageDataStandardResponse<DownloadUpdatePackageResponse> download() {
        return new AdminApiPageDataStandardResponse<>(AdminStaticService.getInstance().getUpgradeService().download(getParamWithEmptyCheck("preUpgradeKey"), Constants.zrLogConfig.getPlugin(UpdateVersionInfoPlugin.class)));
    }

    @ResponseBody
    public AdminApiPageDataStandardResponse<PreCheckVersionResponse> index() throws ParseException {
        return new AdminApiPageDataStandardResponse<>(AdminStaticService.getInstance().getUpgradeService().preUpgradeVersion(true, Constants.zrLogConfig.getPlugin(UpdateVersionInfoPlugin.class), System.currentTimeMillis() + ""));
    }


    @ResponseBody
    public AdminApiPageDataStandardResponse<UpgradeProcessResponse> doUpgrade() {
        return new AdminApiPageDataStandardResponse<>(AdminStaticService.getInstance().getUpgradeService().doUpgrade(getParamWithEmptyCheck("preUpgradeKey")));
    }

}
