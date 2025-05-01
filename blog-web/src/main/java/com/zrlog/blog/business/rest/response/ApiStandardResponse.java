package com.zrlog.blog.business.rest.response;

import com.zrlog.common.rest.response.StandardResponse;

public class ApiStandardResponse<T> extends StandardResponse {

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

    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
