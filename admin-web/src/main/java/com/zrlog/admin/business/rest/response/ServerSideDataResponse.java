package com.zrlog.admin.business.rest.response;

import java.util.Map;

public class ServerSideDataResponse {

    private UserBasicInfoResponse user;
    private Map<String, Object> resourceInfo;
    private Object pageData;
    private String key;
    private String documentTitle;

    public ServerSideDataResponse(UserBasicInfoResponse user, Map<String, Object> resourceInfo, Object pageData, String key, String documentTitle) {
        this.user = user;
        this.resourceInfo = resourceInfo;
        this.pageData = pageData;
        this.key = key;
        this.documentTitle = documentTitle;
    }

    public UserBasicInfoResponse getUser() {
        return user;
    }

    public Map<String, Object> getResourceInfo() {
        return resourceInfo;
    }

    public Object getPageData() {
        return pageData;
    }

    public String getKey() {
        return key;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }
}