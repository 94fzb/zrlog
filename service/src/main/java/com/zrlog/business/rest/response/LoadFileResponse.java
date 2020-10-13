package com.zrlog.business.rest.response;

import com.zrlog.common.rest.response.StandardResponse;

public class LoadFileResponse extends StandardResponse {

    private String fileContent;

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }
}
