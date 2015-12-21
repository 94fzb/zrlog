package com.fzb.blog.util.duoshuo;

import java.util.List;

public class DuoShuoComment {
    private Integer code;
    private List<ResponseEntry> response;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public List<ResponseEntry> getResponse() {
        return response;
    }

    public void setResponse(List<ResponseEntry> response) {
        this.response = response;
    }
}
