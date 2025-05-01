package com.zrlog.admin.business.rest.response;

public class UploadFileResponse {

    private final String url;

    public UploadFileResponse(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
