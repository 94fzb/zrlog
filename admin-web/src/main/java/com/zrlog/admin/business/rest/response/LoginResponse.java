package com.zrlog.admin.business.rest.response;

public class LoginResponse {

    private final String key;

    public LoginResponse(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
