package com.zrlog.admin.web.plugin;

import java.util.Map;

public class LambdaUpdateVersionHandler implements UpdateVersionHandler {

    public LambdaUpdateVersionHandler(Map<String, Object> backend) {

    }

    @Override
    public String getMessage() {
        return "Upgrade by aws-cli";
    }

    @Override
    public boolean isFinish() {
        return false;
    }

    @Override
    public void doHandle() {

    }
}
