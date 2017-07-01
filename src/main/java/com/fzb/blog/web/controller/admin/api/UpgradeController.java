package com.fzb.blog.web.controller.admin.api;

import com.fzb.blog.common.Constants;
import com.fzb.blog.common.response.CheckVersionResponse;
import com.fzb.blog.common.response.DownloadUpdatePackageResponse;
import com.fzb.blog.common.response.UpdateRecordResponse;
import com.fzb.blog.common.response.UpgradeProcessResponse;
import com.fzb.blog.model.WebSite;
import com.fzb.blog.service.CacheService;
import com.fzb.blog.web.controller.BaseController;
import com.fzb.blog.web.plugin.PluginConfig;
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

    private static final String DOWNLOAD_ATTR_KEY_PREFIX = "downing";
    private static final String UPDATE_THREAD_ATTR_KEY = "updateVersionThread";

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
        DownloadProcessHandle handle = getSessionAttr(DOWNLOAD_ATTR_KEY_PREFIX);
        if (handle == null) {
            File file = new File(PathKit.getWebRootPath() + "/WEB-INF/update-temp/" + "zrlog.war");
            file.getParentFile().mkdir();
            handle = new DownloadProcessHandle(file);
            try {
                HttpUtil.getInstance().sendGetRequest(lastVersion().getVersion().getDownloadUrl(), handle, new HashMap<String, String>());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        getSession().setAttribute(DOWNLOAD_ATTR_KEY_PREFIX, handle);
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
        DownloadProcessHandle handle = getSessionAttr(DOWNLOAD_ATTR_KEY_PREFIX);
        File file = handle.getFile();
        UpgradeProcessResponse upgradeProcessResponse = new UpgradeProcessResponse();
        UpdateVersionThread updateVersionThread = getSessionAttr(UPDATE_THREAD_ATTR_KEY);
        if (updateVersionThread == null) {
            updateVersionThread = new UpdateVersionThread(file);
            setSessionAttr(UPDATE_THREAD_ATTR_KEY, updateVersionThread);
            PluginConfig.stopPluginCore();
            updateVersionThread.start();
        }
        upgradeProcessResponse.setMessage(updateVersionThread.getMessage());
        upgradeProcessResponse.setFinish(updateVersionThread.isFinish());
        return upgradeProcessResponse;
    }

}
