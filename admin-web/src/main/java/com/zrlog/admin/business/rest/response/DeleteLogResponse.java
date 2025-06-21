package com.zrlog.admin.business.rest.response;

public class DeleteLogResponse {

    private Boolean delete;

    public DeleteLogResponse(Boolean delete) {
        this.delete = delete;
    }

    public Boolean getDelete() {
        return delete;
    }
}
