package com.zrlog.admin.business.rest.response;

import com.zrlog.admin.business.rest.request.UpdateArticleRequest;

public class LoadEditArticleResponse extends UpdateArticleRequest {

    private String previewUrl;

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public Integer getId() {
        return logId;
    }
}
