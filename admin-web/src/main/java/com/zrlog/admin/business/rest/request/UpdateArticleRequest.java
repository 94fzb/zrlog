package com.zrlog.admin.business.rest.request;

public class UpdateArticleRequest extends CreateArticleRequest {

    private Integer logId;
    private Integer version;

    public Integer getLogId() {
        return logId;
    }

    public void setLogId(Integer logId) {
        this.logId = logId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
