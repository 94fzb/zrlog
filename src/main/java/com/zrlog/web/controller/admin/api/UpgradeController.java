package com.zrlog.web.controller.admin.api;

import com.zrlog.service.CacheService;
import com.zrlog.web.plugin.UpdateVersionThread;
import com.zrlog.web.token.AdminTokenThreadLocal;
import com.zrlog.common.Constants;
import com.zrlog.common.response.CheckVersionResponse;
import com.zrlog.common.response.DownloadUpdatePackageResponse;
import com.zrlog.common.response.UpdateRecordResponse;
import com.zrlog.common.response.UpgradeProcessResponse;
import com.zrlog.model.WebSite;
import com.zrlog.web.controller.BaseController;
import com.zrlog.web.plugin.PluginConfig;
import com.zrlog.web.plugin.UpdateVersionPlugin;
import com.zrlog.web.plugin.Version;
import com.zrlog.web.plugin.type.AutoUpgradeVersionType;
import com.zrlog.web.plugin.DownloadProcessHandle;
import com.hibegin.common.util.http.HttpUtil;
import com.jfinal.config.Plugins;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.IPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UpgradeController extends BaseController {

    private static Map<Integer, DownloadProcessHandle> downloadProcessHandleMap = new ConcurrentHashMap<>();
    private static Map<Integer, UpdateVersionThread> updateVersionThreadMap = new ConcurrentHashMap<>();

    private CacheService cacheService = new CacheService();

    public UpdateRecordResponse setting() {
        Map<String, String[]> tmpParamMap = getParaMap();
        for (Map.Entry<String, String[]> param : tmpParamMap.entrySet()) {
            new WebSite().updateByKV(param.getKey(), param.getValue()[0]);
        }
        cacheService.refreshInitDataCache(this, true);
        cacheService.removeCachedStaticFile();
        UpdateRecordResponse recordResponse = new UpdateRecordResponse();
        recordResponse.setError(0);
        Plugins plugins = (Plugins) JFinal.me().getServletContext().getAttribute("plugins");
        if (AutoUpgradeVersionType.cycle(getParaToInt(Constants.AUTO_UPGRADE_VERSION_KEY)) == AutoUpgradeVersionType.NEVER) {
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

    public DownloadUpdatePackageResponse download() {
        DownloadProcessHandle handle = downloadProcessHandleMap.get(AdminTokenThreadLocal.getUser().getSessionId());
        if (handle == null) {
            File file = new File(PathKit.getWebRootPath() + "/WEB-INF/update-temp/" + "zrlog.war");
            file.getParentFile().mkdir();
            Version version = lastVersion().getVersion();
            handle = new DownloadProcessHandle(version, file);
            try {
                HttpUtil.getInstance().sendGetRequest(version.getDownloadUrl(), handle, new HashMap<String, String>());
            } catch (IOException e) {
                e.printStackTrace();
            }
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
            PluginConfig.stopPluginCore();
            updateVersionThread.start();
        }
        upgradeProcessResponse.setMessage(updateVersionThread.getMessage());
        upgradeProcessResponse.setFinish(updateVersionThread.isFinish());
        upgradeProcessResponse.setBuildId(handle.getVersion().getBuildId());
        upgradeProcessResponse.setVersion(handle.getVersion().getVersion());
        return upgradeProcessResponse;
    }

}
