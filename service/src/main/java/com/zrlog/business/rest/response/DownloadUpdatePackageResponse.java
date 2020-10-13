package com.zrlog.business.rest.response;

import com.zrlog.common.rest.response.StandardResponse;

public class DownloadUpdatePackageResponse extends StandardResponse {

    private int process;

    public int getProcess() {
        return process;
    }

    public void setProcess(int process) {
        this.process = process;
    }
}
