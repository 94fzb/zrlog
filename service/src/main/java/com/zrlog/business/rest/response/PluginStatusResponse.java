package com.zrlog.business.rest.response;

public class PluginStatusResponse {

    private Long code;
    private PluginCoreStatus status;

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public PluginCoreStatus getStatus() {
        return status;
    }

    public void setStatus(PluginCoreStatus status) {
        this.status = status;
    }
}
