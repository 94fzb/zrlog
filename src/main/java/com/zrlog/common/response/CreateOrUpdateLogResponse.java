package com.zrlog.common.response;

public class CreateOrUpdateLogResponse {

    private int error;
    private int logId;
    private String alias;
    private String thumbnail;

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

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
