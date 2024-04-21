package com.zrlog.admin.business.service;

import com.hibegin.common.util.LoggerUtil;
import com.hibegin.common.util.SecurityUtils;
import com.hibegin.common.util.StringUtils;
import com.hibegin.common.util.http.HttpUtil;
import com.hibegin.common.util.http.handle.HttpFileHandle;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.admin.business.rest.response.CheckVersionResponse;
import com.zrlog.admin.business.rest.response.DownloadUpdatePackageResponse;
import com.zrlog.admin.business.rest.response.UpgradeProcessResponse;
import com.zrlog.admin.web.plugin.DockerUpdateVersionThread;
import com.zrlog.admin.web.plugin.UpdateVersionHandler;
import com.zrlog.admin.web.plugin.UpdateVersionPlugin;
import com.zrlog.admin.web.plugin.ZipUpdateVersionThread;
import com.zrlog.admin.web.token.AdminTokenThreadLocal;
import com.zrlog.common.vo.Version;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.ZrLogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class UpgradeService {

    private static final Map<String, Version> versionMap = new ConcurrentHashMap<>();
    private static final Map<String, UpdateVersionHandler> updateVersionThreadMap = new ConcurrentHashMap<>();
    private static final Map<String, HttpFileHandle> downloadProcessHandleMap = new ConcurrentHashMap<>();


    public CheckVersionResponse getCheckVersionResponse(boolean fetchAble, UpdateVersionPlugin plugin) {
        CheckVersionResponse checkVersionResponse = new CheckVersionResponse();
        Version version = plugin.getLastVersion(fetchAble);
        if (Objects.isNull(version)) {
            checkVersionResponse.setUpgrade(false);
            return checkVersionResponse;
        }
        checkVersionResponse.setUpgrade(ZrLogUtil.greatThenCurrentVersion(version.getBuildId(), version.getBuildDate(), version.getVersion()));
        //不在页面展示SNAPSHOT
        version.setVersion(version.getVersion().replaceAll("-SNAPSHOT", ""));
        checkVersionResponse.setVersion(version);
        checkVersionResponse.setDockerMode(ZrLogUtil.isDockerMode());
        return checkVersionResponse;
    }

    public CheckVersionResponse preUpgradeVersion(boolean fetchAble, UpdateVersionPlugin plugin) {
        CheckVersionResponse checkVersionResponse = getCheckVersionResponse(fetchAble, plugin);
        versionMap.put(AdminTokenThreadLocal.getUser().getSessionId(), checkVersionResponse.getVersion());
        return checkVersionResponse;
    }

    private static boolean isMatch(File file, long length, String md5sum) {
        try {
            //先比较文件大小
            if (length > 0 && length != file.length()) {
                return false;
            }
            if (StringUtils.isNotEmpty(md5sum)) {
                return file.exists() && SecurityUtils.md5(new FileInputStream(file)).equals(md5sum);
            } else {
                //如何md5sum没有传的情况，认为文件长度相同就行
                if (length > 0) {
                    return file.length() == length;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public HttpFileHandle createFileHandle() {
        File file = PathUtil.getConfFile("/update-temp/zrlog.zip");
        file.getParentFile().mkdir();
        return new HttpFileHandle(file.getParentFile().toString(), file.getName());
    }

    public DownloadUpdatePackageResponse download() throws IOException, URISyntaxException, InterruptedException, ParseException {
        Version version = versionMap.get(AdminTokenThreadLocal.getUser().getSessionId());
        if (Objects.isNull(version)) {
            LoggerUtil.getLogger(UpgradeService.class).warning("Missing pre check version ");
            new DownloadUpdatePackageResponse(0);
        }
        HttpFileHandle handle = downloadProcessHandleMap.computeIfAbsent(AdminTokenThreadLocal.getUser().getSessionId(), k -> {
            HttpFileHandle fileHandle = createFileHandle();
            CompletableFuture.runAsync(() -> {
                try {
                    HttpUtil.getInstance().sendGetRequest(version.getZipDownloadUrl(), fileHandle, new HashMap<>());
                } catch (IOException | InterruptedException | URISyntaxException e) {
                    LoggerUtil.getLogger(UpgradeService.class).severe("Download file error " + e.getMessage());
                }
            });
            return fileHandle;
        });
        if (Objects.nonNull(handle.getT())) {
            return new DownloadUpdatePackageResponse((int) (version.getZipFileSize() * 100 / handle.getT().length()));
        } else {
            return new DownloadUpdatePackageResponse(0);
        }
    }

    public UpgradeProcessResponse doUpgrade() {
        UpgradeProcessResponse upgradeProcessResponse = new UpgradeProcessResponse();
        String sessionId = AdminTokenThreadLocal.getUser().getSessionId();
        UpdateVersionHandler updateVersionHandler = updateVersionThreadMap.get(sessionId);
        if (Objects.nonNull(updateVersionHandler)) {
            upgradeProcessResponse.setMessage(updateVersionHandler.getMessage());
            upgradeProcessResponse.setFinish(updateVersionHandler.isFinish());
            return upgradeProcessResponse;
        }
        if (ZrLogUtil.isDockerMode()) {
            updateVersionHandler = new DockerUpdateVersionThread(I18nUtil.getBackend());
        } else {
            HttpFileHandle handle = downloadProcessHandleMap.get(sessionId);
            if (handle == null) {
                return new UpgradeProcessResponse();
            }
            File file = handle.getT();
            Version version = versionMap.get(AdminTokenThreadLocal.getUser().getSessionId());
            if (isMatch(file, version.getZipFileSize(), version.getMd5sum())) {
                updateVersionHandler = new ZipUpdateVersionThread(file, I18nUtil.getBackend());
            } else {
                upgradeProcessResponse.setMessage(I18nUtil.getBackendStringFromRes("upgradeDownloadFileError"));
                upgradeProcessResponse.setFinish(false);
                downloadProcessHandleMap.remove(sessionId);
                return upgradeProcessResponse;
            }
        }
        updateVersionHandler.start();
        upgradeProcessResponse.setMessage(updateVersionHandler.getMessage());
        upgradeProcessResponse.setFinish(updateVersionHandler.isFinish());
        updateVersionThreadMap.put(sessionId, updateVersionHandler);
        return upgradeProcessResponse;

    }
}
