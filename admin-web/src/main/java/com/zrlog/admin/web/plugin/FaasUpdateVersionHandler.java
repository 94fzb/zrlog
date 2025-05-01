package com.zrlog.admin.web.plugin;

import com.hibegin.common.util.EnvKit;
import com.zrlog.common.vo.Version;

import java.util.Map;

public class FaasUpdateVersionHandler implements UpdateVersionHandler {

    private final Version version;

    public FaasUpdateVersionHandler(Map<String, Object> backend, Version version) {
        this.version = version;
    }

    @Override
    public String getMessage() {
        String link = "[download upgrade file](" + version.getZipDownloadUrl().replaceFirst(".zip", "-faas.zip") + ")";
        if (EnvKit.isLambda()) {
            return "update by aws-cli, " + link;
        }
        return link;
    }

    @Override
    public boolean isFinish() {
        return false;
    }

    @Override
    public void doHandle() {

    }
}
