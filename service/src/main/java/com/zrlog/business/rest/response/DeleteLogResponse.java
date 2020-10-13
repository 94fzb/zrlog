package com.zrlog.business.rest.response;

import com.zrlog.common.rest.response.StandardResponse;

public class DeleteLogResponse extends StandardResponse {
    private boolean delete;

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }
}
