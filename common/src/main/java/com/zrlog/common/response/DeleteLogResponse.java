package com.zrlog.common.response;

public class DeleteLogResponse extends StandardResponse {
    private boolean delete;

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }
}
