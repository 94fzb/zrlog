package com.zrlog.common.rest.response;

import com.zrlog.common.Constants;

public class ApiStandardResponse<T> extends StandardResponse {

    private final String pageBuildId;
    private T data;

    public ApiStandardResponse() {
        this(null);
    }


    public ApiStandardResponse(T data) {
        this(data, "");
    }

    public ApiStandardResponse(T data, String message) {
        this.data = data;
        super.setMessage(message);
        this.pageBuildId = Constants.zrLogConfig.getAdminResource().getStaticResourceBuildId();
    }

    public String getPageBuildId() {
        return pageBuildId;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
