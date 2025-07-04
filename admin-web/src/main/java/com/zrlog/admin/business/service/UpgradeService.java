package com.zrlog.admin.business.service;

import com.hibegin.common.util.*;
import com.hibegin.common.util.http.HttpUtil;
import com.hibegin.common.util.http.handle.HttpFileHandle;
import com.zrlog.admin.business.exception.DownloadUpgradeFileException;
import com.zrlog.admin.business.rest.response.CheckVersionResponse;
import com.zrlog.admin.business.rest.response.DownloadUpdatePackageResponse;
import com.zrlog.admin.business.rest.response.PreCheckVersionResponse;
import com.zrlog.admin.business.rest.response.UpgradeProcessResponse;
import com.zrlog.admin.web.plugin.*;
import com.zrlog.business.plugin.UpdateVersionInfoPlugin;
import com.zrlog.common.Constants;
import com.zrlog.common.type.RunMode;
import com.zrlog.common.vo.Version;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.ThreadUtils;
import com.zrlog.util.ZrLogUtil;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class UpgradeService {

    private static final Map<String, Version> versionMap = new ConcurrentHashMap<>();
    private static final Map<String, UpdateVersionHandler> updateVersionThreadMap = new ConcurrentHashMap<>();
    private static final Map<String, HttpFileHandle> downloadProcessHandleMap = new ConcurrentHashMap<>();


    public CheckVersionResponse getCheckVersionResponse(boolean fetchAble, UpdateVersionInfoPlugin plugin) {
        CheckVersionResponse checkVersionResponse = new CheckVersionResponse();
        if (Objects.isNull(plugin)) {
            checkVersionResponse.setUpgrade(false);
            return checkVersionResponse;
        }
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
        checkVersionResponse.setSystemServiceMode(ZrLogUtil.isSystemServiceMode());
        return checkVersionResponse;
    }

    public PreCheckVersionResponse getPreCheckVersionResponse(boolean fetchAble, UpdateVersionInfoPlugin plugin, String preUpgradeKey) {
        CheckVersionResponse checkVersionResponse = getCheckVersionResponse(fetchAble, plugin);
        PreCheckVersionResponse preCheckVersionResponse = BeanUtil.convert(checkVersionResponse, PreCheckVersionResponse.class);
        preCheckVersionResponse.setPreUpgradeKey(preUpgradeKey);
        return preCheckVersionResponse;

    }

    public PreCheckVersionResponse preUpgradeVersion(boolean fetchAble, UpdateVersionInfoPlugin plugin, String preUpgradeKey) {
        PreCheckVersionResponse checkVersionResponse = getPreCheckVersionResponse(fetchAble, plugin, preUpgradeKey);
        if (Objects.nonNull(checkVersionResponse.getVersion())) {
            versionMap.put(preUpgradeKey, checkVersionResponse.getVersion());
        }
        return checkVersionResponse;
    }

    private static boolean isErrorFile(File file, long length, String md5sum) {
        try {
            //先比较文件大小
            if (length > 0 && length != file.length()) {
                return true;
            }
            if (StringUtils.isNotEmpty(md5sum)) {
                return !file.exists() || !SecurityUtils.md5ByFile(file).equals(md5sum);
            } else {
                //如何md5sum没有传的情况，认为文件长度相同就行
                if (length > 0) {
                    return file.length() != length;
                }
            }
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    public HttpFileHandle createFileHandle() {
        File file = new File(Constants.zrLogConfig.getUpdater().getUpdateTempPath() + "/zrlog.zip");
        file.getParentFile().mkdir();
        return new HttpFileHandle(file.getParentFile().toString(), file.getName());
    }

    public DownloadUpdatePackageResponse download(String preUpgradeKey, UpdateVersionInfoPlugin plugin) {
        Version version = versionMap.computeIfAbsent(preUpgradeKey, k -> {
            return getPreCheckVersionResponse(true, plugin, preUpgradeKey).getVersion();
        });
        if (Objects.isNull(version)) {
            LoggerUtil.getLogger(UpgradeService.class).warning("Missing pre check version ");
            return new DownloadUpdatePackageResponse(0);
        }
        HttpFileHandle handle = downloadProcessHandleMap.computeIfAbsent(preUpgradeKey, k -> {
            HttpFileHandle fileHandle = createFileHandle();
            ThreadUtils.start(() -> {
                try {
                    String downloadUrl = ZrLogUtil.isWarMode() ? version.getWarDownloadUrl() : version.getZipDownloadUrl();
                    HttpUtil.getInstance().sendGetRequest(downloadUrl, fileHandle, new HashMap<>());
                } catch (IOException | InterruptedException | URISyntaxException e) {
                    LoggerUtil.getLogger(UpgradeService.class).severe("Download file error " + e.getMessage());
                }
            });
            return fileHandle;
        });
        if (Objects.nonNull(handle.getT())) {
            int process = (int) (handle.getT().length() * 100 / version.getZipFileSize());
            if (process >= 100) {
                if (ZrLogUtil.isWarMode()) {
                    if (isErrorFile(handle.getT(), version.getWarFileSize(), version.getWarMd5sum())) {
                        throw new DownloadUpgradeFileException();
                    }
                } else if (isErrorFile(handle.getT(), version.getZipFileSize(), version.getZipMd5sum())) {
                    throw new DownloadUpgradeFileException();
                }
            }
            return new DownloadUpdatePackageResponse(Math.min(process, 100));
        } else {
            return new DownloadUpdatePackageResponse(0);
        }
    }

    public UpgradeProcessResponse doUpgrade(String preUpgradeKey) {
        UpdateVersionHandler updateVersionHandler = updateVersionThreadMap.get(preUpgradeKey);
        if (Objects.nonNull(updateVersionHandler)) {
            return new UpgradeProcessResponse(updateVersionHandler.isFinish(), updateVersionHandler.getMessage());
        }
        if (ZrLogUtil.isDockerMode()) {
            updateVersionHandler = new DockerUpdateVersionHandle(I18nUtil.getBackend());
        } else if (ZrLogUtil.isSystemServiceMode()) {
            updateVersionHandler = new SystemServiceUpdateVersionHandle(I18nUtil.getBackend());
        } else if (EnvKit.isLambda()) {
            updateVersionHandler = new LambdaUpdateVersionHandler(I18nUtil.getBackend());
        } else {
            HttpFileHandle handle = downloadProcessHandleMap.get(preUpgradeKey);
            if (handle == null) {
                return new UpgradeProcessResponse(false, "");
            }
            if (ZrLogUtil.isWarMode()) {
                updateVersionHandler = new WarUpdateVersionHandle(handle.getT(), I18nUtil.getBackend(), preUpgradeKey, versionMap.get(preUpgradeKey));
            } else {
                updateVersionHandler = new ZipUpdateVersionHandle(handle.getT(), I18nUtil.getBackend(), versionMap.get(preUpgradeKey));
            }
        }
        updateVersionHandler.doHandle();
        updateVersionThreadMap.put(preUpgradeKey, updateVersionHandler);
        return new UpgradeProcessResponse(updateVersionHandler.isFinish(), updateVersionHandler.getMessage());

    }
}
