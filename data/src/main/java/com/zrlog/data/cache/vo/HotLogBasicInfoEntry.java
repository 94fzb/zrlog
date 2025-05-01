package com.zrlog.data.cache.vo;

import java.io.Serializable;

public class HotLogBasicInfoEntry implements Serializable {

    private Long logId;
    private String title;
    private String alias;

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
