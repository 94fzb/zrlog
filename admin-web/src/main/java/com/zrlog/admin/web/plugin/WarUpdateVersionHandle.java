package com.zrlog.admin.web.plugin;

import com.zrlog.common.Constants;
import com.zrlog.common.Updater;
import com.zrlog.common.vo.Version;

import java.io.File;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;


public class WarUpdateVersionHandle implements Serializable, UpdateVersionHandler {
    private String message = "";
    private final File file;
    private boolean finish;
    private final Map<String, Object> backendRes;
    private String backup;
    private String upgradeFile;
    private final String upgradeKey;
    private final Version version;

    public WarUpdateVersionHandle(File file, Map<String, Object> backendRes, String upgradeKey, Version version) {
        this.file = file;
        this.backendRes = backendRes;
        this.upgradeKey = upgradeKey;
        this.version = version;
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
        try {
            Updater updater = Constants.zrLogConfig.getUpdater();
            if (Objects.isNull(backup)) {
                backup = updater.backup();
            }
            sb.append("- ").append(backendRes.get("upgradeBackup")).append(backup).append("\n");
            message = sb.toString();
            if (Objects.isNull(upgradeFile)) {
                updater.restartProcessAsync(version);
                upgradeFile = updater.buildUpgradeFile(file.toString(), upgradeKey);
            }
            sb.append("- ").append(backendRes.get("upgradeMerge")).append(upgradeFile).append("\n");
            sb.append("- ").append(backendRes.get("upgradeRestarting")).append("\n");
            message = sb.toString();
            finish = true;
        } catch (Exception e) {
            sb.append(backendRes.get("upgradeError")).append(" ").append(e.getMessage());
            message = sb.toString();
        }
    }
}
