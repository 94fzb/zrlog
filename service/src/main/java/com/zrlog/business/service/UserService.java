package com.zrlog.business.service;

import com.hibegin.common.util.SecurityUtils;
import com.hibegin.common.util.StringUtils;
import com.zrlog.business.rest.request.UpdatePasswordRequest;
import com.zrlog.business.rest.request.LoginRequest;
import com.zrlog.business.rest.request.UpdateAdminRequest;
import com.zrlog.business.rest.response.LoginResponse;
import com.zrlog.business.rest.response.UpdateRecordResponse;
import com.zrlog.model.User;
import com.zrlog.util.I18nUtil;

import java.util.Objects;

public class UserService {

    public UpdateRecordResponse updatePassword(int currentUserId, UpdatePasswordRequest updatePasswordRequest) {
        UpdateRecordResponse updateRecordResponse = new UpdateRecordResponse();
        if (StringUtils.isNotEmpty(updatePasswordRequest.getOldPassword()) && StringUtils.isNotEmpty(updatePasswordRequest.getNewPassword())) {
            String dbPassword = new User().getPasswordByUserId(currentUserId);
            String oldPassword = updatePasswordRequest.getOldPassword();
            // compare oldPassword
            if (SecurityUtils.md5(oldPassword).equals(dbPassword)) {
                new User().updatePassword(currentUserId, SecurityUtils.md5(updatePasswordRequest.getNewPassword()));
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
        if (StringUtils.isNotEmpty(loginRequest.getUserName()) && StringUtils.isNotEmpty(loginRequest.getPassword())) {
            String dbPassword = new User().getPasswordByUserName(loginRequest.getUserName().toLowerCase());
            if (dbPassword == null || !Objects.equals(dbPassword.toLowerCase(), loginRequest.getPassword().toLowerCase())) {
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
