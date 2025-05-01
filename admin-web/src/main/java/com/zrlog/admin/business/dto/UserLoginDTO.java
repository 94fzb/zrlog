package com.zrlog.admin.business.dto;

import com.zrlog.admin.business.rest.response.UserBasicInfoResponse;

public class UserLoginDTO {

    private UserBasicInfoResponse userBasicInfoResponse;
    private String secretKey;
    private Integer id;

    public UserBasicInfoResponse getUserBasicInfoResponse() {
        return userBasicInfoResponse;
    }

    public void setUserBasicInfoResponse(UserBasicInfoResponse userBasicInfoResponse) {
        this.userBasicInfoResponse = userBasicInfoResponse;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
