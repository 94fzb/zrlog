package com.fzb.blog.common.response;

public class UpgradeProcessResponse {

    private int error;
    private int process;
    private String message;
    private boolean needRestart;

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public int getProcess() {
        return process;
    }

    public void setProcess(int process) {
        this.process = process;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isNeedRestart() {
        return needRestart;
    }

    public void setNeedRestart(boolean needRestart) {
        this.needRestart = needRestart;
    }
}
