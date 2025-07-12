package com.zrlog.admin.web.controller.api;

import com.hibegin.http.annotation.ResponseBody;
import com.hibegin.http.server.web.Controller;
import com.zrlog.admin.business.rest.response.SystemResponse;
import com.zrlog.common.Constants;
import com.zrlog.common.rest.response.ApiStandardResponse;
import com.zrlog.util.ZrLogUtil;

import java.sql.SQLException;

import static com.zrlog.admin.util.SystemInfoUtils.serverInfo;
import static com.zrlog.admin.util.SystemInfoUtils.systemIOInfoVO;

public class AdminSystemController extends Controller {

    @ResponseBody
    public ApiStandardResponse<SystemResponse> index() throws SQLException {
        return new ApiStandardResponse<>(new SystemResponse(systemIOInfoVO(), serverInfo(getRequest()), ZrLogUtil.isDockerMode(), Constants.runMode.isNative()));
    }
}
