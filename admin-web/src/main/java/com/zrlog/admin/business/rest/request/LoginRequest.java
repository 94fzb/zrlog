package com.zrlog.admin.business.rest.request;

import com.hibegin.common.util.StringUtils;
import com.zrlog.admin.business.exception.UserNameAndPasswordRequiredException;
import com.zrlog.common.Validator;

public class LoginRequest implements Validator {

    private String password;
    private String userName;
    private Boolean https;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Boolean getHttps() {
        return https;
    }

    public void setHttps(Boolean https) {
        this.https = https;
    }

    @Override
    public void doValid() {
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) {
            throw new UserNameAndPasswordRequiredException();
        }
    }
}
