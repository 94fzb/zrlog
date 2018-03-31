package com.zrlog.common.response;

public class LoadFileResponse extends StandardResponse {

    private String fileContent;

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }
}
