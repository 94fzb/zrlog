package com.zrlog.service;

import com.hibegin.common.util.SecurityUtils;
import com.hibegin.common.util.StringUtils;
import com.zrlog.common.request.ChangePasswordRequest;
import com.zrlog.common.request.LoginRequest;
import com.zrlog.common.request.UpdateAdminRequest;
import com.zrlog.common.response.LoginResponse;
import com.zrlog.common.response.UpdateRecordResponse;
import com.zrlog.model.User;
import com.zrlog.util.I18nUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.atomic.AtomicInteger;

public class UserService {

    private AdminTokenService adminTokenService = new AdminTokenService();

    private static AtomicInteger sessionAtomicInteger = new AtomicInteger();

    public UpdateRecordResponse updatePassword(ChangePasswordRequest changePasswordRequest) {
        UpdateRecordResponse updateRecordResponse = new UpdateRecordResponse();
        if (StringUtils.isNotEmpty(changePasswordRequest.getOldPassword()) && StringUtils.isNotEmpty(changePasswordRequest.getNewPassword())) {
            String dbPassword = User.dao.getPasswordByUserId(AdminTokenThreadLocal.getUserId());
            String oldPassword = changePasswordRequest.getOldPassword();
            // compare oldPassword
            if (SecurityUtils.md5(oldPassword).equals(dbPassword)) {
                User.dao.updatePassword(AdminTokenThreadLocal.getUserId(), SecurityUtils.md5(changePasswordRequest.getNewPassword()));
                updateRecordResponse.setMessage(I18nUtil.getStringFromRes("changePasswordSuccess"));
            } else {
                updateRecordResponse.setError(1);
                updateRecordResponse.setMessage(I18nUtil.getStringFromRes("oldPasswordError"));
            }
        } else {
            updateRecordResponse.setError(1);
            updateRecordResponse.setMessage(I18nUtil.getStringFromRes("argsError"));
        }
        return updateRecordResponse;
    }

    public LoginResponse login(LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
        LoginResponse loginResponse = new LoginResponse();
        if (StringUtils.isNotEmpty(loginRequest.getUserName()) &&
                StringUtils.isNotEmpty(loginRequest.getPassword()) && StringUtils.isNotEmpty(loginRequest.getKey())) {
            String dbPassword = User.dao.getPasswordByUserName(loginRequest.getUserName().toLowerCase());
            if (dbPassword != null && SecurityUtils.md5(loginRequest.getKey() + ":" + dbPassword).equals(loginRequest.getPassword())) {
                adminTokenService.setAdminToken(User.dao.getIdByUserName(loginRequest.getUserName().toLowerCase()),
                        sessionAtomicInteger.incrementAndGet(), loginRequest.getHttps() ? "https" : "http", request, response);
            } else {
                loginResponse.setError(1);
                loginResponse.setMessage(I18nUtil.getStringFromRes("userNameOrPasswordError"));
            }
        } else {
            loginResponse.setError(1);
            loginResponse.setMessage(I18nUtil.getStringFromRes("userNameAndPasswordRequired"));
        }
        return loginResponse;
    }


    public Object update(UpdateAdminRequest updateAdminRequest) {
        User.dao.updateEmailUserNameHeaderByUserId(updateAdminRequest.getEmail(), updateAdminRequest.getUserName(), updateAdminRequest.getHeader(), updateAdminRequest.getUserId());
        return User.dao.findById(updateAdminRequest.getUserId());
    }
}
