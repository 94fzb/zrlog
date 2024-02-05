package com.zrlog.admin.business.service;

import com.zrlog.admin.business.rest.response.CheckVersionResponse;
import com.zrlog.admin.business.rest.response.DownloadUpdatePackageResponse;
import com.zrlog.admin.business.rest.response.UpgradeProcessResponse;
import com.zrlog.admin.web.plugin.UpdateVersionHandler;
import com.zrlog.admin.web.plugin.UpdateVersionPlugin;
import com.zrlog.common.vo.Version;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UpgradeService {

    private static final Map<Integer, Version> versionMap = new ConcurrentHashMap<>();
    private static final Map<Integer, UpdateVersionHandler> updateVersionThreadMap = new ConcurrentHashMap<>();

    public CheckVersionResponse getCheckVersionResponse(boolean fetchAble, UpdateVersionPlugin plugins) {
        CheckVersionResponse checkVersionResponse = new CheckVersionResponse();

        Version version = plugins.getLastVersion(fetchAble);
        if (version != null) {
            checkVersionResponse.setUpgrade(true);
            //不在页面展示SNAPSHOT
            version.setVersion(version.getVersion().replaceAll("-SNAPSHOT", ""));
            checkVersionResponse.setVersion(version);
        } else {
            checkVersionResponse.setUpgrade(false);
        }
        return checkVersionResponse;
    }

    public DownloadUpdatePackageResponse download(UpdateVersionPlugin plugin) throws IOException {

        return new DownloadUpdatePackageResponse();
    }

    public UpgradeProcessResponse doUpgrade() {
        /*DownloadProcessHandle handle = downloadProcessHandleMap.get(AdminTokenThreadLocal.getUser().getSessionId());
        if (handle == null) {
            return new UpgradeProcessResponse();
        }
        UpgradeProcessResponse upgradeProcessResponse = new UpgradeProcessResponse();
        File file = handle.getFile();
        int sessionId = AdminTokenThreadLocal.getUser().getSessionId();
        UpdateVersionHandler updateVersionHandler = updateVersionThreadMap.get(sessionId);
        if (updateVersionHandler == null) {
            if (handle.isMatch()) {
                updateVersionHandler = new ZipUpdateVersionThread(file);
                updateVersionThreadMap.put(AdminTokenThreadLocal.getUser().getSessionId(), updateVersionHandler);
                PluginCoreProcess.getInstance().stopPluginCore();
                updateVersionHandler.start();
                upgradeProcessResponse.setMessage(updateVersionHandler.getMessage());
                upgradeProcessResponse.setFinish(updateVersionHandler.isFinish());
            } else {
                upgradeProcessResponse.setMessage("更新文件下载失败，请重新手动执行更新向导");
                downloadProcessHandleMap.remove(AdminTokenThreadLocal.getUser().getSessionId());
            }
        } else {
            upgradeProcessResponse.setMessage(updateVersionHandler.getMessage());
            upgradeProcessResponse.setFinish(updateVersionHandler.isFinish());
        }
        upgradeProcessResponse.setBuildId(versionMap.get(sessionId).getBuildId());
        upgradeProcessResponse.setVersion(versionMap.get(sessionId).getVersion());
        return upgradeProcessResponse;*/
        return new UpgradeProcessResponse();

    }
}
