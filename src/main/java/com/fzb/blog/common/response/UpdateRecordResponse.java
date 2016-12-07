package com.fzb.blog.common.response;

public class UpdateRecordResponse {

    private int error;
    private String message;

    public UpdateRecordResponse() {
    }

    public UpdateRecordResponse(Boolean success) {
        this.error = success ? 0 : 1;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
