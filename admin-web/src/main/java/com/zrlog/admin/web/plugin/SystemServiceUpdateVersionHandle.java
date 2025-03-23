package com.zrlog.admin.web.plugin;

import com.zrlog.business.service.InstallService;

import java.util.Map;

public class SystemServiceUpdateVersionHandle implements UpdateVersionHandler {

    private String message;
    private boolean finish;
    private final Map<String, Object> blogRes;

    public SystemServiceUpdateVersionHandle(Map<String, Object> blogRes) {
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
        message = InstallService.renderMd((String) blogRes.get("systemServiceUpgradeTips"));
        finish = true;
    }
}
