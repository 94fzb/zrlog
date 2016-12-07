package com.fzb.blog.common.response;

public class CreateOrUpdateLogResponse {

    private int error;
    private int logId;
    private String alias;

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
