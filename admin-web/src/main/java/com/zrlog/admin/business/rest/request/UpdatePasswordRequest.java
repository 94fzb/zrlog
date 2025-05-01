package com.zrlog.admin.business.rest.request;

import com.zrlog.common.Validator;
import com.zrlog.common.exception.ArgsException;

import java.util.Objects;

public class UpdatePasswordRequest implements Validator {

    private String oldPassword;
    private String newPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    @Override
    public void doValid() {
        if (Objects.isNull(oldPassword) || oldPassword.trim().isEmpty()) {
            throw new ArgsException("oldPassword");
        }
        if (Objects.isNull(newPassword) || newPassword.trim().isEmpty()) {
            throw new ArgsException("newPassword");
        }
    }
}
