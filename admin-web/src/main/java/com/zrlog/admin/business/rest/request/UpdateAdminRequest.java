package com.zrlog.admin.business.rest.request;

import com.zrlog.admin.business.exception.ArgsException;
import com.zrlog.common.Validator;

import java.util.Objects;

public class UpdateAdminRequest implements Validator {

    private String userName;
    private String email;
    private String header;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    @Override
    public void doValid() {
        if (Objects.isNull(userName) || userName.trim().isEmpty()) {
            throw new ArgsException("userName");
        }
    }
}
