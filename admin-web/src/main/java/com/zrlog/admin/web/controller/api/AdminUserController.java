package com.zrlog.admin.web.controller.api;

import com.hibegin.common.util.StringUtils;
import com.hibegin.http.annotation.ResponseBody;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.zrlog.admin.business.exception.AdminAuthException;
import com.zrlog.admin.business.rest.request.UpdateAdminRequest;
import com.zrlog.admin.business.rest.request.UpdatePasswordRequest;
import com.zrlog.admin.business.rest.response.UpdateRecordResponse;
import com.zrlog.admin.business.rest.response.UserBasicInfoResponse;
import com.zrlog.admin.business.service.UserService;
import com.zrlog.admin.web.annotation.RefreshCache;
import com.zrlog.admin.web.token.AdminTokenThreadLocal;
import com.zrlog.common.controller.BaseController;
import com.zrlog.common.rest.response.ApiStandardResponse;
import com.zrlog.common.vo.AdminTokenVO;
import com.zrlog.util.I18nUtil;

import java.sql.SQLException;
import java.util.Objects;

public class AdminUserController extends BaseController {

    private final UserService userService;

    public AdminUserController() {
        this.userService = new UserService();
    }

    public AdminUserController(HttpRequest request, HttpResponse response) {
        super(request, response);
        this.userService = new UserService();
    }

    @ResponseBody
    public ApiStandardResponse<UserBasicInfoResponse> index() throws SQLException {
        AdminTokenVO adminTokenVO = AdminTokenThreadLocal.getUser();
        if (Objects.isNull(adminTokenVO)) {
            throw new AdminAuthException();
        }
        return new ApiStandardResponse<>(userService.getUserInfo(adminTokenVO.getUserId(), adminTokenVO.getSessionId(), request));
    }

    @RefreshCache
    @ResponseBody
    public UpdateRecordResponse update() throws SQLException {
        UpdateAdminRequest updateAdminRequest = getRequestBodyWithNullCheck(UpdateAdminRequest.class);
        UpdateRecordResponse updateRecordResponse = new UpdateRecordResponse();
        if (StringUtils.isEmpty(updateAdminRequest.getUserName())) {
            updateRecordResponse.setError(1);
        } else {
            userService.update(AdminTokenThreadLocal.getUserId(), updateAdminRequest);
            updateRecordResponse.setMessage(I18nUtil.getBackendStringFromRes("updatePersonInfoSuccess"));
        }
        return updateRecordResponse;
    }

    @ResponseBody
    public UpdateRecordResponse updatePassword() throws SQLException {
        return userService.updatePassword(AdminTokenThreadLocal.getUserId(),
                getRequestBodyWithNullCheck(UpdatePasswordRequest.class));
    }
}
