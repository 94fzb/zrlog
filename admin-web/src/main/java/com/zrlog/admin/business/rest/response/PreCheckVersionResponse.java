package com.zrlog.admin.business.rest.response;

public class PreCheckVersionResponse extends CheckVersionResponse {

    private String preUpgradeKey;

    public String getPreUpgradeKey() {
        return preUpgradeKey;
    }

    public void setPreUpgradeKey(String preUpgradeKey) {
        this.preUpgradeKey = preUpgradeKey;
    }
}
