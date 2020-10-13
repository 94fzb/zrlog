package com.zrlog.business.rest.response;

import com.zrlog.common.rest.response.StandardResponse;

public class CreateCommentResponse extends StandardResponse {

    private String alias;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
