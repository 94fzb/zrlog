package com.zrlog.business.rest.request;

public class UpdateArticleRequest extends CreateArticleRequest {

    private Integer logId;

    public Integer getLogId() {
        return logId;
    }

    public void setLogId(Integer logId) {
        this.logId = logId;
    }
}
