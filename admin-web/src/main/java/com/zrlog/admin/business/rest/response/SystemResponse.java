package com.zrlog.admin.business.rest.response;

import com.zrlog.admin.util.ServerInfo;

import java.util.List;

public class SystemResponse {

    private List<ServerInfo> serverInfos2;
    private List<ServerInfo> serverInfos;
    private Boolean dockerMode;
    private Boolean nativeImageMode;

    public SystemResponse(List<ServerInfo> serverInfos2, List<ServerInfo> serverInfos, Boolean dockerMode, Boolean nativeImageMode) {
        this.serverInfos2 = serverInfos2;
        this.serverInfos = serverInfos;
        this.dockerMode = dockerMode;
        this.nativeImageMode = nativeImageMode;
    }

    public List<ServerInfo> getServerInfos2() {
        return serverInfos2;
    }

    public List<ServerInfo> getServerInfos() {
        return serverInfos;
    }

    public Boolean getDockerMode() {
        return dockerMode;
    }

    public Boolean getNativeImageMode() {
        return nativeImageMode;
    }
}
