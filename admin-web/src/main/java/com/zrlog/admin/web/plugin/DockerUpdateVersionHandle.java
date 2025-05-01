package com.zrlog.admin.web.plugin;

import java.util.Map;

public class DockerUpdateVersionHandle implements UpdateVersionHandler {

    private String message;
    private boolean finish;
    private final Map<String, Object> blogRes;

    public DockerUpdateVersionHandle(Map<String, Object> blogRes) {
        this.blogRes = blogRes;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public boolean isFinish() {
        return finish;
    }

    @Override
    public void doHandle() {
        message = blogRes.get("dockerUpgradeTips").toString();
        finish = true;
    }
}
