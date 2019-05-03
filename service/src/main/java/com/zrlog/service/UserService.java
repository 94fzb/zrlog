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

public class UserService {

    public UpdateRecordResponse updatePassword(int currentUserId, ChangePasswordRequest changePasswordRequest) {
        UpdateRecordResponse updateRecordResponse = new UpdateRecordResponse();
        if (StringUtils.isNotEmpty(changePasswordRequest.getOldPassword()) && StringUtils.isNotEmpty(changePasswordRequest.getNewPassword())) {
            String dbPassword = new User().getPasswordByUserId(currentUserId);
            String oldPassword = changePasswordRequest.getOldPassword();
            // compare oldPassword
            if (SecurityUtils.md5(oldPassword).equals(dbPassword)) {
                new User().updatePassword(currentUserId, SecurityUtils.md5(changePasswordRequest.getNewPassword()));
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

    public LoginResponse login(LoginRequest loginRequest) {
        LoginResponse loginResponse = new LoginResponse();
        if (StringUtils.isNotEmpty(loginRequest.getUserName()) &&
                StringUtils.isNotEmpty(loginRequest.getPassword()) && StringUtils.isNotEmpty(loginRequest.getKey())) {
            String dbPassword = new User().getPasswordByUserName(loginRequest.getUserName().toLowerCase());
            if (dbPassword == null || !SecurityUtils.md5(loginRequest.getKey() + ":" + dbPassword).equals(loginRequest.getPassword())) {
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
        new User().updateEmailUserNameHeaderByUserId(updateAdminRequest.getEmail(), updateAdminRequest.getUserName(), updateAdminRequest.getHeader(), updateAdminRequest.getUserId());
        return new User().findById(updateAdminRequest.getUserId());
    }
}
