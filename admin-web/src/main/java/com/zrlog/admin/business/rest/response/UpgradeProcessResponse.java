package com.zrlog.admin.business.rest.response;

public class UpgradeProcessResponse {

    private Boolean finish;
    private String message;

    public Boolean getFinish() {
        return finish;
    }

    public void setFinish(Boolean finish) {
        this.finish = finish;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
