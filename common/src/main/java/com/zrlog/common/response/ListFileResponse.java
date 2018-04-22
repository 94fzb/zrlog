package com.zrlog.common.response;

import java.util.List;

public class ListFileResponse extends StandardResponse {

    private List<String> files;

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }
}
