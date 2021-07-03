package com.zrlog.admin.business.rest.response;

public class UserBasicInfoResponse {

    private String header;
    private String userName;
    private String email;
    private CheckVersionResponse lastVersion;

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

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

    public CheckVersionResponse getLastVersion() {
        return lastVersion;
    }

    public void setLastVersion(CheckVersionResponse lastVersion) {
        this.lastVersion = lastVersion;
    }
}
