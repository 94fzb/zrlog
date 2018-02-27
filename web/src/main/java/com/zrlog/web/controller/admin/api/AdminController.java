package com.zrlog.web.controller.admin.api;

import com.hibegin.common.util.SecurityUtils;
import com.hibegin.common.util.StringUtils;
import com.zrlog.common.request.ChangePasswordRequest;
import com.zrlog.common.request.LoginRequest;
import com.zrlog.common.request.UpdateAdminRequest;
import com.zrlog.common.response.LoginResponse;
import com.zrlog.common.response.UpdateRecordResponse;
import com.zrlog.model.User;
import com.zrlog.util.I18NUtil;
import com.zrlog.util.ZrLogUtil;
import com.zrlog.web.annotation.RefreshCache;
import com.zrlog.web.controller.BaseController;
import com.zrlog.service.AdminTokenService;
import com.zrlog.service.AdminTokenThreadLocal;

import java.util.concurrent.atomic.AtomicInteger;

public class AdminController extends BaseController {

    private AdminTokenService adminTokenService = new AdminTokenService();

    private static AtomicInteger sessionAtomicInteger = new AtomicInteger();

    public LoginResponse login() {
        LoginRequest loginRequest = ZrLogUtil.convertRequestBody(getRequest(), LoginRequest.class);
        LoginResponse loginResponse = new LoginResponse();
        if (StringUtils.isNotEmpty(loginRequest.getUserName()) &&
                StringUtils.isNotEmpty(loginRequest.getPassword()) && StringUtils.isNotEmpty(loginRequest.getKey())) {
            String dbPassword = User.dao.getPasswordByUserName(loginRequest.getUserName().toLowerCase());
            if (dbPassword != null && SecurityUtils.md5(loginRequest.getKey() + ":" + dbPassword).equals(loginRequest.getPassword())) {
                adminTokenService.setAdminToken(User.dao.getIdByUserName(loginRequest.getUserName().toLowerCase()), sessionAtomicInteger.incrementAndGet(), getRequest(), getResponse());
            } else {
                loginResponse.setError(1);
                loginResponse.setMessage(I18NUtil.getStringFromRes("userNameOrPasswordError", getRequest()));
            }
        } else {
            loginResponse.setError(1);
            loginResponse.setMessage(I18NUtil.getStringFromRes("userNameAndPasswordRequired", getRequest()));
        }
        return loginResponse;
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
        updateRecordResponse.setMessage(I18NUtil.getStringFromRes("updatePersonInfoSuccess", getRequest()));
        return updateRecordResponse;
    }

    public UpdateRecordResponse changePassword() {
        UpdateRecordResponse updateRecordResponse = new UpdateRecordResponse();
        ChangePasswordRequest changePasswordRequest = ZrLogUtil.convertRequestBody(getRequest(), ChangePasswordRequest.class);
        if (isNotNullOrNotEmptyStr(changePasswordRequest.getOldPassword(), changePasswordRequest.getNewPassword())) {
            String dbPassword = User.dao.getPasswordByUserId(AdminTokenThreadLocal.getUserId());
            String oldPassword = changePasswordRequest.getOldPassword();
            // compare oldPassword
            if (SecurityUtils.md5(oldPassword).equals(dbPassword)) {
                User.dao.updatePassword(AdminTokenThreadLocal.getUserId(), SecurityUtils.md5(changePasswordRequest.getNewPassword()));
                updateRecordResponse.setMessage(I18NUtil.getStringFromRes("changePasswordSuccess", getRequest()));
            } else {
                updateRecordResponse.setError(1);
                updateRecordResponse.setMessage(I18NUtil.getStringFromRes("oldPasswordError", getRequest()));
            }
        } else {
            updateRecordResponse.setError(1);
            updateRecordResponse.setMessage(I18NUtil.getStringFromRes("argsError", getRequest()));
        }
        return updateRecordResponse;
    }
}
