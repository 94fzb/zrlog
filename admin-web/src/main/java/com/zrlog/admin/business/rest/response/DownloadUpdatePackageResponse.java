package com.zrlog.admin.business.rest.response;

public class DownloadUpdatePackageResponse {

    private Integer process;

    public DownloadUpdatePackageResponse(Integer process) {
        this.process = process;
    }

    public Integer getProcess() {
        return process;
    }

    public void setProcess(Integer process) {
        this.process = process;
    }
}
