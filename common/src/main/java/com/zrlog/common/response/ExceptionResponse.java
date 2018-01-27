package com.zrlog.common.response;

public class ExceptionResponse extends StandardResponse {

    private String stack;

    public String getStack() {
        return stack;
    }

    public void setStack(String stack) {
        this.stack = stack;
    }
}
