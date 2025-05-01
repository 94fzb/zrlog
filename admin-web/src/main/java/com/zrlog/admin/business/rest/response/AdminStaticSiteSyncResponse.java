package com.zrlog.admin.business.rest.response;

public class AdminStaticSiteSyncResponse {

    private Boolean synced;

    public AdminStaticSiteSyncResponse() {
    }

    public AdminStaticSiteSyncResponse(Boolean synced) {
        this.synced = synced;
    }

    public Boolean getSynced() {
        return synced;
    }

    public void setSynced(Boolean synced) {
        this.synced = synced;
    }
}
