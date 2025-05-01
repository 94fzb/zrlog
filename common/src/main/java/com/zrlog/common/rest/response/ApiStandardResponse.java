package com.zrlog.common.rest.response;

import com.zrlog.common.Constants;
import com.zrlog.common.ZrLogConfig;

import java.util.Objects;

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
        ZrLogConfig zrLogConfig = Constants.zrLogConfig;
        if (Objects.nonNull(zrLogConfig) && Objects.nonNull(zrLogConfig.getAdminResource())) {
            this.pageBuildId = zrLogConfig.getAdminResource().getStaticResourceBuildId();
        } else {
            this.pageBuildId = "";
        }
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
