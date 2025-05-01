package com.zrlog.admin.business.rest.response;

import java.util.Map;

public class ServerSideDataResponse<T> extends AdminApiPageDataStandardResponse<T> {

    private final UserBasicInfoResponse user;
    private final Map<String, Object> resourceInfo;
    private final String key;

    public ServerSideDataResponse(UserBasicInfoResponse user, Map<String, Object> resourceInfo, T pageData, String key, String documentTitle) {
        super(pageData, "");
        this.user = user;
        this.resourceInfo = resourceInfo;
        this.key = key;
        this.documentTitle = documentTitle;
    }

    public UserBasicInfoResponse getUser() {
        return user;
    }

    public Map<String, Object> getResourceInfo() {
        return resourceInfo;
    }

    public String getKey() {
        return key;
    }

}