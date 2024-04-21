package com.zrlog.admin.web.plugin;

import com.zrlog.business.service.InstallService;

import java.util.Map;

public class DockerUpdateVersionThread extends Thread implements UpdateVersionHandler {

    private String message;
    private boolean finish;
    private final Map<String, Object> blogRes;

    public DockerUpdateVersionThread(Map<String, Object> blogRes) {
        this.blogRes = blogRes;
    }

    @Override
    public void run() {
        message = InstallService.renderMd((String) blogRes.get("dockerUpgradeTips"));
        finish = true;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public boolean isFinish() {
        return finish;
    }
}
