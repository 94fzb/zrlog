package com.zrlog.web.controller.admin.api;

import com.zrlog.common.request.ChangePasswordRequest;
import com.zrlog.common.request.LoginRequest;
import com.zrlog.common.request.UpdateAdminRequest;
import com.zrlog.common.response.LoginResponse;
import com.zrlog.common.response.UpdateRecordResponse;
import com.zrlog.model.User;
import com.zrlog.service.AdminTokenThreadLocal;
import com.zrlog.service.UserService;
import com.zrlog.util.I18NUtil;
import com.zrlog.util.ZrLogUtil;
import com.zrlog.web.annotation.RefreshCache;
import com.zrlog.web.controller.BaseController;

public class AdminController extends BaseController {

    private UserService userService = new UserService();

    public LoginResponse login() {
        return userService.login(ZrLogUtil.convertRequestBody(getRequest(), LoginRequest.class), getRequest(), getResponse());
    }

    @RefreshCache
    public UpdateRecordResponse update() {
        UpdateAdminRequest updateAdminRequest = ZrLogUtil.convertRequestBody(getRequest(), UpdateAdminRequest.class);
        UpdateRecordResponse updateRecordResponse = new UpdateRecordResponse();
        if (updateAdminRequest != null) {
            Integer userId = AdminTokenThreadLocal.getUserId();
            User.dao.updateEmailUserNameHeaderByUserId(updateAdminRequest.getEmail(), updateAdminRequest.getUserName(), updateAdminRequest.getHeader(), userId);
            getRequest().setAttribute("user", User.dao.findById(userId));
        }
        updateRecordResponse.setMessage(I18NUtil.getStringFromRes("updatePersonInfoSuccess"));
        return updateRecordResponse;
    }

    public UpdateRecordResponse changePassword() {
        return userService.updatePassword(ZrLogUtil.convertRequestBody(getRequest(), ChangePasswordRequest.class));
    }
}
