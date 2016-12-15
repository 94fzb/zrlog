package com.fzb.blog.web.controller.admin.api;

import com.fzb.blog.common.Constants;
import com.fzb.blog.common.response.CheckVersionResponse;
import com.fzb.blog.common.response.DownloadUpdatePackageResponse;
import com.fzb.blog.common.response.UpdateRecordResponse;
import com.fzb.blog.common.response.UpgradeProcessResponse;
import com.fzb.blog.model.WebSite;
import com.fzb.blog.service.CacheService;
import com.fzb.blog.web.controller.BaseController;
import com.fzb.blog.web.plugin.UpdateVersionPlugin;
import com.fzb.blog.web.plugin.UpdateVersionThread;
import com.fzb.blog.web.plugin.Version;
import com.fzb.blog.web.plugin.type.AutoUpgradeVersionType;
import com.fzb.common.util.http.HttpUtil;
import com.fzb.common.util.http.handle.DownloadProcessHandle;
import com.jfinal.config.Plugins;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.IPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UpgradeController extends BaseController {

    private CacheService cacheService = new CacheService();

    public UpdateRecordResponse setting() {
        Map<String, String[]> tmpParamMap = getParaMap();
        for (Map.Entry<String, String[]> param : tmpParamMap.entrySet()) {
            new WebSite().updateByKV(param.getKey(), param.getValue()[0]);
        }
        cacheService.cleanCache();
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
        DownloadProcessHandle handle = (DownloadProcessHandle) getSession().getAttribute("downing");
        if (handle == null) {
            File file = new File(PathKit.getWebRootPath() + "/WEB-INF/update-temp/" + "zrlog.war");
            file.getParentFile().mkdir();
            handle = new DownloadProcessHandle(file);
            try {
                HttpUtil.sendGetRequest(lastVersion().getVersion().getDownloadUrl(), handle, new HashMap<String, String>());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        getSession().setAttribute("downing", handle);
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
        DownloadProcessHandle handle = (DownloadProcessHandle) getSession().getAttribute("downing");
        File file = handle.getFile();
        UpgradeProcessResponse upgradeProcessResponse = new UpgradeProcessResponse();
        UpdateVersionThread updateVersionThread = (UpdateVersionThread) JFinal.me().getServletContext().getAttribute("updateVersionThread");
        if (updateVersionThread == null) {
            updateVersionThread = new UpdateVersionThread(file);
            JFinal.me().getServletContext().setAttribute("updateVersionThread", updateVersionThread);
            updateVersionThread.start();
        }
        upgradeProcessResponse.setMessage(updateVersionThread.getMessage());
        return upgradeProcessResponse;
    }

}
