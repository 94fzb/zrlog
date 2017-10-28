package com.zrlog.common.response;

public class UpgradeProcessResponse {

    private int error;
    private String message;
    private boolean finish;
    private String buildId;
    private String version;
    private boolean needRestart;

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
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

    public boolean isFinish() {
        return finish;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    public String getBuildId() {
        return buildId;
    }

    public void setBuildId(String buildId) {
        this.buildId = buildId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
