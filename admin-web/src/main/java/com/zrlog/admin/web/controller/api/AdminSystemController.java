package com.zrlog.admin.web.controller.api;

import com.hibegin.http.annotation.ResponseBody;
import com.hibegin.http.server.web.Controller;
import com.zrlog.admin.business.rest.response.SystemResponse;
import com.zrlog.common.Constants;
import com.zrlog.common.rest.response.ApiStandardResponse;
import com.zrlog.common.type.RunMode;
import com.zrlog.util.ZrLogUtil;

import java.util.Objects;

import static com.zrlog.admin.util.SystemInfoUtils.systemIOInfoVO;
import static com.zrlog.admin.util.SystemInfoUtils.serverInfo;

public class AdminSystemController extends Controller {

    @ResponseBody
    public ApiStandardResponse<SystemResponse> index() {
        return new ApiStandardResponse<>(new SystemResponse(systemIOInfoVO(), serverInfo(), ZrLogUtil.isDockerMode(), Objects.equals(Constants.runMode, RunMode.NATIVE)));
    }
}
