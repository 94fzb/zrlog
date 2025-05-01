package com.zrlog.admin.business.rest.response;

public class PreCheckVersionResponse extends CheckVersionResponse {

    private Boolean onlineUpgradable;
    private String disableUpgradeReason;
    private Boolean dockerMode;
    private Boolean systemServiceMode;
    private String preUpgradeKey;

    public String getPreUpgradeKey() {
        return preUpgradeKey;
    }

    public void setPreUpgradeKey(String preUpgradeKey) {
        this.preUpgradeKey = preUpgradeKey;
    }

    public Boolean getOnlineUpgradable() {
        return onlineUpgradable;
    }

    public void setOnlineUpgradable(Boolean onlineUpgradable) {
        this.onlineUpgradable = onlineUpgradable;
    }

    public String getDisableUpgradeReason() {
        return disableUpgradeReason;
    }

    public void setDisableUpgradeReason(String disableUpgradeReason) {
        this.disableUpgradeReason = disableUpgradeReason;
    }

    public Boolean getDockerMode() {
        return dockerMode;
    }

    @Deprecated
    public void setDockerMode(Boolean dockerMode) {
        this.dockerMode = dockerMode;
    }

    public Boolean getSystemServiceMode() {
        return systemServiceMode;
    }

    @Deprecated
    public void setSystemServiceMode(Boolean systemServiceMode) {
        this.systemServiceMode = systemServiceMode;
    }
}
