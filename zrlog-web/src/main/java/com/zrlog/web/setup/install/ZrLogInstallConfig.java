package com.zrlog.web.setup.install;

import com.hibegin.common.util.EnvKit;
import com.zrlog.business.rest.response.UpgradeProcessResponse;
import com.zrlog.business.service.UpgradeService;
import com.zrlog.business.version.UpgradeVersionHandler;
import com.zrlog.common.Constants;
import com.zrlog.common.Updater;
import com.zrlog.common.UpdaterTypeEnum;
import com.zrlog.common.ZrLogConfig;
import com.zrlog.common.updater.UpdateVersionTimerTask;
import com.zrlog.common.updater.handle.FaasUpdateVersionHandler;
import com.zrlog.common.vo.Version;
import com.zrlog.install.business.response.InstallUpgradeResult;
import com.zrlog.install.business.response.LastVersionInfo;
import com.zrlog.install.business.service.InstallUpgradeAction;
import com.zrlog.install.web.InstallAction;
import com.zrlog.install.web.config.DefaultInstallConfig;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.ZrLogUtil;

import java.io.File;
import java.util.Map;
import java.util.Objects;

public class ZrLogInstallConfig extends DefaultInstallConfig {

    private final ZrLogConfig zrLogConfig;
    private final File dbPropertiesFile;
    private final LastVersionInfo lastVersionInfo;
    private final Version lastVersion;
    private final Updater updater;
    private final InstallAction installAction;

    public ZrLogInstallConfig(ZrLogConfig zrLogConfig, File dbPropertiesFile, File lockFile, Updater updater) {
        this.zrLogConfig = zrLogConfig;
        this.dbPropertiesFile = dbPropertiesFile;
        this.updater = updater;
        this.lastVersion = prefetchVersion();
        this.lastVersionInfo = zrLogConfig.isInstalled() || zrLogConfig.isTest() ? null :
                toLastVersionInfo(updater, lastVersion);
        this.installAction = new ZrLogInstallAction(zrLogConfig, lockFile);
    }

    private Version prefetchVersion() {
        if (zrLogConfig.isInstalled() || zrLogConfig.isTest()) {
            return null;
        }
        UpdateVersionTimerTask versionTimerTask = new UpdateVersionTimerTask(!BlogBuildInfoUtil.isRelease(), Constants.DEFAULT_LANGUAGE);
        versionTimerTask.run();
        return versionTimerTask.getVersion();
    }

    static LastVersionInfo toLastVersionInfo(Updater updater, Version lastVersion) {
        LastVersionInfo lastVersionInfo = new LastVersionInfo();
        if (Objects.isNull(lastVersion)) {
            lastVersionInfo.setLatestVersion(true);
            return lastVersionInfo;
        }
        boolean upgradable = ZrLogUtil.greatThenCurrentVersion(lastVersion.getBuildId(),
                lastVersion.getBuildDate(), lastVersion.getVersion());
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
    public String defaultTemplatePath() {
        return Constants.DEFAULT_TEMPLATE_PATH;
    }

    @Override
    public String getZrLogSqlVersion() {
        return UpgradeVersionHandler.SQL_VERSION + "";
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

    @Override
    public InstallUpgradeAction getUpgradeAction() {
        return new InstallUpgradeAction() {
            @Override
            public boolean isSupported() {
                return !zrLogConfig.isInstalled() && Objects.nonNull(updater)
                        && !ZrLogUtil.isDockerMode() && !ZrLogUtil.isSystemServiceMode()
                        && (!EnvKit.isFaaSMode() || FaasUpdateVersionHandler.isOnlineUpgradeSupported());
            }

            @Override
            public InstallUpgradeResult upgrade(ProgressListener progressListener) throws Exception {
                UpgradeProcessResponse response = new UpgradeService().doUpgrade(lastVersion, updater,
                        progressListener::onProgress, I18nUtil.getBackend());
                return new InstallUpgradeResult(Boolean.TRUE.equals(response.getFinish()), response.getMessage());
            }
        };
    }

    @Override
    public String getJdbcUrlQueryStr(String dbType, Map<String, String[]> paramMap) {
        if (Objects.equals(dbType, "mysql")) {
            return Constants.MYSQL_JDBC_PARAMS;
        }
        return "";
    }

    @Override
    public boolean isAskConfig() {
        return zrLogConfig.isAskConfig();
    }

    @Override
    public boolean isMissingConfig() {
        return zrLogConfig.isMissingConfig();
    }
}
