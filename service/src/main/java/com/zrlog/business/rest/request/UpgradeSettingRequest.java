package com.zrlog.business.rest.request;

public class UpgradeSettingRequest {

    private boolean upgradePreview;

    private int autoUpgradeVersion;

    public boolean isUpgradePreview() {
        return upgradePreview;
    }

    public void setUpgradePreview(boolean upgradePreview) {
        this.upgradePreview = upgradePreview;
    }

    public int getAutoUpgradeVersion() {
        return autoUpgradeVersion;
    }

    public void setAutoUpgradeVersion(int autoUpgradeVersion) {
        this.autoUpgradeVersion = autoUpgradeVersion;
    }
}
