package com.zrlog.admin.business.rest.response;


import com.zrlog.common.vo.Version;

import java.io.Serializable;

public class CheckVersionResponse implements Serializable {

    private Boolean upgrade;
    private Version version;
    private Boolean dockerMode;

    public Boolean getUpgrade() {
        return upgrade;
    }

    public void setUpgrade(Boolean upgrade) {
        this.upgrade = upgrade;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public Boolean getDockerMode() {
        return dockerMode;
    }

    public void setDockerMode(Boolean dockerMode) {
        this.dockerMode = dockerMode;
    }
}
