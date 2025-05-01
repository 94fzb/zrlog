package com.zrlog.admin.business.rest.response;

public class TemplateDownloadResponse {

    private final String url;

    public TemplateDownloadResponse(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
