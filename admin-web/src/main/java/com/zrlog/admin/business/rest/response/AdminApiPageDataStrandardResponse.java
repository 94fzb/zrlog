package com.zrlog.admin.business.rest.response;

import com.zrlog.common.rest.response.ApiStandardResponse;

public class AdminApiPageDataStrandardResponse<T> extends ApiStandardResponse {

    private String documentTitle;

    public AdminApiPageDataStrandardResponse() {
    }

    public AdminApiPageDataStrandardResponse(T data) {
        super(data);
    }


    public AdminApiPageDataStrandardResponse(T data, String message) {
        super(data, message);
    }


    public String getDocumentTitle() {
        return documentTitle;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }
}
