package com.zrlog.business.rest.response;

import com.zrlog.common.rest.response.StandardResponse;

public class VersionResponse extends StandardResponse {

    private String version;
    private String buildId;
    private String changelog;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getBuildId() {
        return buildId;
    }

    public void setBuildId(String buildId) {
        this.buildId = buildId;
    }

    public String getChangelog() {
        return changelog;
    }

    public void setChangelog(String changelog) {
        this.changelog = changelog;
    }
}
