package com.zrlog.web.setup.install;

import com.hibegin.common.util.SecurityUtils;
import com.zrlog.admin.web.plugin.UpdateVersionTimerTask;
import com.zrlog.business.service.DbUpgradeService;
import com.zrlog.common.Constants;
import com.zrlog.common.Updater;
import com.zrlog.common.UpdaterTypeEnum;
import com.zrlog.common.ZrLogConfig;
import com.zrlog.common.vo.Version;
import com.zrlog.install.business.response.LastVersionInfo;
import com.zrlog.install.web.InstallAction;
import com.zrlog.install.web.InstallConstants;
import com.zrlog.install.web.config.DefaultInstallConfig;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.ZrLogUtil;

import java.io.File;
import java.util.Objects;

public class ZrLogInstallConfig extends DefaultInstallConfig {

    private final ZrLogConfig zrLogConfig;
    private final File dbPropertiesFile;
    private final LastVersionInfo lastVersionInfo;
    private final Updater updater;
    private final InstallAction installAction;

    public ZrLogInstallConfig(ZrLogConfig zrLogConfig, File dbPropertiesFile, File lockFile, Updater updater) {
        this.zrLogConfig = zrLogConfig;
        this.dbPropertiesFile = dbPropertiesFile;
        this.updater = updater;
        this.lastVersionInfo = prefetchVersion(updater);
        this.installAction = new ZrLogInstallAction(zrLogConfig, lockFile);
    }

    private LastVersionInfo prefetchVersion(Updater updater) {
        if (zrLogConfig.isInstalled()) {
            return null;
        }
        UpdateVersionTimerTask versionTimerTask = new UpdateVersionTimerTask(!BlogBuildInfoUtil.isRelease(), "zh_CN");
        versionTimerTask.run();
        Version lastVersion = versionTimerTask.getVersion();
        boolean upgradable = ZrLogUtil.greatThenCurrentVersion(lastVersion.getBuildId(), lastVersion.getBuildDate(), lastVersion.getVersion());
        return getLastVersionInfo(updater, upgradable, lastVersion);
    }

    private static LastVersionInfo getLastVersionInfo(Updater updater, boolean upgradable, Version lastVersion) {
        LastVersionInfo lastVersionInfo = new LastVersionInfo();
        lastVersionInfo.setLatestVersion(!upgradable);
        if (lastVersionInfo.getLatestVersion()) {
            return lastVersionInfo;
        }
        lastVersionInfo.setNewVersion(lastVersion.getVersion());
        if (Objects.nonNull(updater) && updater.getType() == UpdaterTypeEnum.WAR) {
            lastVersionInfo.setDownloadUrl(lastVersion.getWarDownloadUrl());
        } else {
            lastVersionInfo.setDownloadUrl(lastVersion.getZipDownloadUrl());
        }
        lastVersionInfo.setChangeLog(lastVersion.getChangeLog());
        return lastVersionInfo;
    }

    @Override
    public InstallAction getAction() {
        return installAction;
    }

    @Override
    public boolean isWarMode() {
        return Objects.nonNull(updater) && updater.getType() == UpdaterTypeEnum.WAR;
    }

    @Override
    public String getAcceptLanguage() {
        return I18nUtil.getCurrentLocale();
    }

    @Override
    public String encryptPassword(String password) {
        return SecurityUtils.md5(password);
    }

    @Override
    public String defaultTemplatePath() {
        return Constants.DEFAULT_TEMPLATE_PATH;
    }

    @Override
    public String getZrLogSqlVersion() {
        return DbUpgradeService.SQL_VERSION + "";
    }

    @Override
    public File getDbPropertiesFile() {
        return dbPropertiesFile;
    }

    @Override
    public String getBuildVersion() {
        return BlogBuildInfoUtil.getVersion();
    }

    @Override
    public LastVersionInfo getLastVersionInfo() {
        return lastVersionInfo;
    }
}
