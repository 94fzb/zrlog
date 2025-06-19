package com.zrlog.admin.business.rest.response;

import java.util.Set;

public class UserBasicInfoResponse {

    private String header;
    private String userName;
    private String email;
    private CheckVersionResponse lastVersion;
    private String key;
    private Set<String> cacheableApiUris;

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public CheckVersionResponse getLastVersion() {
        return lastVersion;
    }

    public void setLastVersion(CheckVersionResponse lastVersion) {
        this.lastVersion = lastVersion;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Set<String> getCacheableApiUris() {
        return cacheableApiUris;
    }

    public void setCacheableApiUris(Set<String> cacheableApiUris) {
        this.cacheableApiUris = cacheableApiUris;
    }
}
