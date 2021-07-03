package com.zrlog.admin.business.rest.request;

public class LoginRequest {

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
}
