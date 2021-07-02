package com.zrlog.common.rest.response;

public class ApiStandardResponse extends StandardResponse {

    private Object data;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
