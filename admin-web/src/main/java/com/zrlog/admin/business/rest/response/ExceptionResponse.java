package com.zrlog.admin.business.rest.response;

import com.zrlog.common.rest.response.StandardResponse;

public class ExceptionResponse extends StandardResponse {

    private String stack;

    public String getStack() {
        return stack;
    }

    public void setStack(String stack) {
        this.stack = stack;
    }
}
