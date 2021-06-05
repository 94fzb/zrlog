package com.zrlog.admin.business.service;

import com.hibegin.common.util.SecurityUtils;
import com.hibegin.common.util.StringUtils;
import com.zrlog.admin.business.exception.*;
import com.zrlog.admin.business.rest.request.LoginRequest;
import com.zrlog.admin.business.rest.request.UpdateAdminRequest;
import com.zrlog.admin.business.rest.request.UpdatePasswordRequest;
import com.zrlog.admin.business.rest.response.UpdateRecordResponse;
import com.zrlog.model.User;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.ZrLogUtil;

import java.util.Objects;

public class UserService {

    public UpdateRecordResponse updatePassword(int currentUserId, UpdatePasswordRequest updatePasswordRequest) {
        if (ZrLogUtil.isPreviewMode()) {
            throw new PermissionErrorException();
        }
        if (StringUtils.isNotEmpty(updatePasswordRequest.getOldPassword()) && StringUtils.isNotEmpty(updatePasswordRequest.getNewPassword())) {
            String dbPassword = new User().getPasswordByUserId(currentUserId);
            String oldPassword = updatePasswordRequest.getOldPassword();
            // compare oldPassword
            if (SecurityUtils.md5(oldPassword).equals(dbPassword)) {
                new User().updatePassword(currentUserId, SecurityUtils.md5(updatePasswordRequest.getNewPassword()));
                UpdateRecordResponse updateRecordResponse = new UpdateRecordResponse();
                updateRecordResponse.setMessage(I18nUtil.getBlogStringFromRes("changePasswordSuccess"));
                return updateRecordResponse;
            } else {
                throw new OldPasswordException();
            }
        } else {
            throw new ArgsException();
        }
    }

    public void login(LoginRequest loginRequest) {
        if (StringUtils.isNotEmpty(loginRequest.getUserName()) && StringUtils.isNotEmpty(loginRequest.getPassword())) {
            String dbPassword = new User().getPasswordByUserName(loginRequest.getUserName().toLowerCase());
            if (dbPassword == null || !Objects.equals(dbPassword.toLowerCase(), loginRequest.getPassword().toLowerCase())) {
                throw new UserNameOrPasswordException();
            }
        } else {
            throw new UserNameAndPasswordRequiredException();
        }
    }


    public Object update(int userId, UpdateAdminRequest updateAdminRequest) {
        if (ZrLogUtil.isPreviewMode()) {
            throw new PermissionErrorException();
        }
        new User().updateEmailUserNameHeaderByUserId(updateAdminRequest.getEmail(), updateAdminRequest.getUserName(), updateAdminRequest.getHeader(), userId);
        return new User().findById(userId);
    }
}
