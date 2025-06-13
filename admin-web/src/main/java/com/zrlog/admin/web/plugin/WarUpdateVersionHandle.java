package com.zrlog.admin.web.plugin;

import com.hibegin.common.util.ZipUtil;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.common.Constants;
import com.zrlog.common.Updater;
import com.zrlog.common.vo.Version;

import java.io.File;
import java.io.Serializable;
import java.util.*;


public class WarUpdateVersionHandle implements Serializable, UpdateVersionHandler {
    private String message = "";
    private final File file;
    private boolean finish;
    private final Map<String, Object> backendRes;
    private final Version upgradeVersion;

    public WarUpdateVersionHandle(File file, Map<String, Object> backendRes, Version upgradeVersion) {
        this.file = file;
        this.backendRes = backendRes;
        this.upgradeVersion = upgradeVersion;
    }

    /**
     * 提示更新进度
     */
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
        StringBuilder sb = new StringBuilder();
        sb.append(backendRes.get("upgradeUnzipping")).append(" ").append(file.getName()).append("<br/>");
        try {
            ZipUtil.unZip(file.toString(), Constants.zrLogConfig.getUpdater().getUnzipPath());
        } catch (Exception e) {
            sb.append(backendRes.get("unzipError")).append(" ").append(e.getMessage());
            message = sb.toString();
            return;
        }
        try {
            Updater updater = Constants.zrLogConfig.getUpdater();

            updater.backup();
            //sb.append(backendRes.get("upgradeUnzipped")).append("<br/>");
            sb.append(backendRes.get("upgradeRestarting")).append("<br/>");
            message = sb.toString();
            if (finish) {
                return;
            }
            updater.buildUpgradeFile();
            finish = true;
        } catch (Exception e) {
            sb.append(backendRes.get("upgradeError")).append(" ").append(e.getMessage());
            message = sb.toString();
        } finally {
            file.delete();
        }
    }
}
