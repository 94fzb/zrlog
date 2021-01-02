package com.zrlog.admin.web.controller.api;

import com.hibegin.common.util.StringUtils;
import com.hibegin.common.util.http.HttpUtil;
import com.hibegin.common.util.http.handle.DownloadProcessHandle;
import com.jfinal.config.Plugins;
import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.IPlugin;
import com.zrlog.admin.web.plugin.*;
import com.zrlog.admin.web.token.AdminTokenThreadLocal;
import com.zrlog.admin.business.rest.response.CheckVersionResponse;
import com.zrlog.admin.business.rest.response.DownloadUpdatePackageResponse;
import com.zrlog.admin.business.rest.response.UpgradeProcessResponse;
import com.zrlog.common.Constants;
import com.zrlog.common.vo.Version;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UpgradeController extends Controller {

    private static final Map<Integer, DownloadProcessHandle> downloadProcessHandleMap = new ConcurrentHashMap<>();
    private static final Map<Integer, Version> versionMap = new ConcurrentHashMap<>();
    private static final Map<Integer, UpdateVersionHandler> updateVersionThreadMap = new ConcurrentHashMap<>();

    public DownloadUpdatePackageResponse download() throws IOException {
        DownloadProcessHandle handle = downloadProcessHandleMap.get(AdminTokenThreadLocal.getUser().getSessionId());
        if (handle == null) {
            File file = new File(PathKit.getWebRootPath() + "/WEB-INF/update-temp/" + "zrlog." + (Constants.IN_JAR ? "zip" : "war"));
            file.getParentFile().mkdir();
            Version version = lastVersion().getVersion();
            handle = new DownloadProcessHandle(file, Constants.IN_JAR ? version.getZipFileSize() : version.getFileSize(), Constants.IN_JAR ? version.getZipMd5sum() : version.getMd5sum());
            HttpUtil.getInstance().sendGetRequest(Constants.IN_JAR ? version.getZipDownloadUrl() : version.getDownloadUrl(), handle, new HashMap<>());
            versionMap.put(AdminTokenThreadLocal.getUser().getSessionId(), version);
            downloadProcessHandleMap.put(AdminTokenThreadLocal.getUser().getSessionId(), handle);
        }
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
                    if (StringUtils.isEmpty(version.getChangeLog())) {
                        version.setChangeLog("- 很懒");
                    }
                } else {
                    checkVersionResponse.setUpgrade(false);
                }
            }
        }
        if (checkVersionResponse.getVersion() != null) {
            //不在页面展示SNAPSHOT
            checkVersionResponse.getVersion().setVersion(checkVersionResponse.getVersion().getVersion().replaceAll("-SNAPSHOT", ""));
        }
        return checkVersionResponse;
    }

    public CheckVersionResponse checkNewVersion() {
        return getCheckVersionResponse(true);
    }

    public UpgradeProcessResponse doUpgrade() {
        DownloadProcessHandle handle = downloadProcessHandleMap.get(AdminTokenThreadLocal.getUser().getSessionId());
        UpgradeProcessResponse upgradeProcessResponse = new UpgradeProcessResponse();
        if (handle != null) {
            File file = handle.getFile();
            int sessionId = AdminTokenThreadLocal.getUser().getSessionId();
            UpdateVersionHandler updateVersionHandler = updateVersionThreadMap.get(sessionId);
            if (updateVersionHandler == null) {
                if (handle.isMatch()) {
                    updateVersionHandler = Constants.IN_JAR ? new ZipUpdateVersionThread(file) : new WarUpdateVersionThread(file);
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
        }
        return upgradeProcessResponse;
    }

}
