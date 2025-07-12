package com.zrlog.business.rest.response;

import java.util.List;

public class PluginStatusResponse {

    private Long code;
    private PluginCoreStatus status;
    private List<String> runningPlugins;

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

    public List<String> getRunningPlugins() {
        return runningPlugins;
    }

    public void setRunningPlugins(List<String> runningPlugins) {
        this.runningPlugins = runningPlugins;
    }
}
