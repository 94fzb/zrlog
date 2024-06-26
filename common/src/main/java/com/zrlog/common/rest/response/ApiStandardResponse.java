package com.zrlog.common.rest.response;

public class ApiStandardResponse<T> extends StandardResponse {

    public ApiStandardResponse() {
    }

    public ApiStandardResponse(T data) {
        this.data = data;
    }


    public ApiStandardResponse(T data, String message) {
        this.data = data;
        super.setMessage(message);
    }

    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
