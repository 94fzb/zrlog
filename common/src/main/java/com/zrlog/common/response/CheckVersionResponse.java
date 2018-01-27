package com.zrlog.common.response;


import com.zrlog.common.vo.Version;

import java.io.Serializable;

public class CheckVersionResponse extends StandardResponse implements Serializable {

    private boolean upgrade;
    private Version version;

    public boolean isUpgrade() {
        return upgrade;
    }

    public void setUpgrade(boolean upgrade) {
        this.upgrade = upgrade;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }
}
