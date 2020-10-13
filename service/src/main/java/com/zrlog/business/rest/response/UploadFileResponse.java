package com.zrlog.business.rest.response;

import com.zrlog.common.rest.response.StandardResponse;

public class UploadFileResponse extends StandardResponse {
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
