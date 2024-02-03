package com.zrlog.common.rest.response;

public class ApiStandardResponse extends StandardResponse {

    public ApiStandardResponse() {
    }

    public ApiStandardResponse(Object data) {
        this.data = data;
    }

    private Object data;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
