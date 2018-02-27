package com.zrlog.web.controller.admin.api;

import com.hibegin.common.util.http.HttpUtil;
import com.jfinal.config.Plugins;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.IPlugin;
import com.zrlog.common.request.UpgradeSettingRequest;
import com.zrlog.common.response.CheckVersionResponse;
import com.zrlog.common.response.DownloadUpdatePackageResponse;
import com.zrlog.common.response.UpdateRecordResponse;
import com.zrlog.common.response.UpgradeProcessResponse;
import com.zrlog.common.type.AutoUpgradeVersionType;
import com.zrlog.common.vo.Version;
import com.zrlog.model.WebSite;
import com.zrlog.service.PluginCoreProcess;
import com.zrlog.util.ZrLogUtil;
import com.zrlog.web.annotation.RefreshCache;
import com.zrlog.web.controller.BaseController;
import com.zrlog.web.plugin.*;
import com.zrlog.service.AdminTokenThreadLocal;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UpgradeController extends BaseController {

    private static Map<Integer, DownloadProcessHandle> downloadProcessHandleMap = new ConcurrentHashMap<>();
    private static Map<Integer, UpdateVersionThread> updateVersionThreadMap = new ConcurrentHashMap<>();

    @RefreshCache
    public UpdateRecordResponse setting() {
        UpdateRecordResponse recordResponse = new UpdateRecordResponse();
        UpgradeSettingRequest upgradeSettingRequest = ZrLogUtil.convertRequestBody(getRequest(), UpgradeSettingRequest.class);
        new WebSite().updateByKV("autoUpgradeVersion", upgradeSettingRequest.getAutoUpgradeVersion());
        new WebSite().updateByKV("upgradePreview", upgradeSettingRequest.isUpgradePreview());
        recordResponse.setError(0);
        Plugins plugins = (Plugins) JFinal.me().getServletContext().getAttribute("plugins");
        if (AutoUpgradeVersionType.cycle(upgradeSettingRequest.getAutoUpgradeVersion()) == AutoUpgradeVersionType.NEVER) {
            for (IPlugin plugin : plugins.getPluginList()) {
                if (plugin instanceof UpdateVersionPlugin) {
                    plugin.stop();
                }
            }
        } else {
            for (IPlugin plugin : plugins.getPluginList()) {
                if (plugin instanceof UpdateVersionPlugin) {
                    plugin.start();
                }
            }
        }
        return recordResponse;
    }

    public DownloadUpdatePackageResponse download() throws IOException {
        DownloadProcessHandle handle = downloadProcessHandleMap.get(AdminTokenThreadLocal.getUser().getSessionId());
        if (handle == null) {
            File file = new File(PathKit.getWebRootPath() + "/WEB-INF/update-temp/" + "zrlog.war");
            file.getParentFile().mkdir();
            Version version = lastVersion().getVersion();
            handle = new DownloadProcessHandle(version, file);
            HttpUtil.getInstance().sendGetRequest(version.getDownloadUrl(), handle, new HashMap<String, String>());
        }
        downloadProcessHandleMap.put(AdminTokenThreadLocal.getUser().getSessionId(), handle);
        DownloadUpdatePackageResponse downloadUpdatePackageResponse = new DownloadUpdatePackageResponse();
        downloadUpdatePackageResponse.setProcess(handle.getProcess());
        return downloadUpdatePackageResponse;
    }

    public CheckVersionResponse lastVersion() {
        return getCheckVersionResponse(false);
    }

    private CheckVersionResponse getCheckVersionResponse(boolean fetchAble) {
        Plugins plugins = (Plugins) JFinal.me().getServletContext().getAttribute("plugins");
        CheckVersionResponse checkVersionResponse = new CheckVersionResponse();
        for (IPlugin plugin : plugins.getPluginList()) {
            if (plugin instanceof UpdateVersionPlugin) {
                Version version = ((UpdateVersionPlugin) plugin).getLastVersion(fetchAble);
                if (version != null) {
                    checkVersionResponse.setUpgrade(true);
                    checkVersionResponse.setVersion(version);
                }
            }
        }
        return checkVersionResponse;
    }

    public CheckVersionResponse checkNewVersion() {
        return getCheckVersionResponse(true);
    }

    public UpgradeProcessResponse doUpgrade() {
        DownloadProcessHandle handle = downloadProcessHandleMap.get(AdminTokenThreadLocal.getUser().getSessionId());
        File file = handle.getFile();
        UpgradeProcessResponse upgradeProcessResponse = new UpgradeProcessResponse();
        UpdateVersionThread updateVersionThread = updateVersionThreadMap.get(AdminTokenThreadLocal.getUser().getSessionId());
        if (updateVersionThread == null) {
            updateVersionThread = new UpdateVersionThread(file);
            updateVersionThreadMap.put(AdminTokenThreadLocal.getUser().getSessionId(), updateVersionThread);
            PluginCoreProcess.getInstance().stopPluginCore();
            updateVersionThread.start();
        }
        upgradeProcessResponse.setMessage(updateVersionThread.getMessage());
        upgradeProcessResponse.setFinish(updateVersionThread.isFinish());
        upgradeProcessResponse.setBuildId(handle.getVersion().getBuildId());
        upgradeProcessResponse.setVersion(handle.getVersion().getVersion());
        return upgradeProcessResponse;
    }

}
