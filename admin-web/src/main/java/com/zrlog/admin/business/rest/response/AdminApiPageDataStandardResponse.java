package com.zrlog.admin.business.rest.response;

import com.zrlog.common.rest.response.ApiStandardResponse;

public class AdminApiPageDataStandardResponse<T> extends ApiStandardResponse<T> {

    private String documentTitle;

    public AdminApiPageDataStandardResponse() {
        this(null);
    }

    public AdminApiPageDataStandardResponse(T data) {
        this(data, "");
    }

    public AdminApiPageDataStandardResponse(T data, String message) {
        super(data, message);
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }
}
