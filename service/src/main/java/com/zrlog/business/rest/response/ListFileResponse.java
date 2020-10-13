package com.zrlog.business.rest.response;

import com.zrlog.common.rest.response.StandardResponse;

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
