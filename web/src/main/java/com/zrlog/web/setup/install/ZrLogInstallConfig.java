package com.zrlog.web.setup.install;

import com.hibegin.common.util.SecurityUtils;
import com.zrlog.admin.web.plugin.UpdateVersionInfoPlugin;
import com.zrlog.business.service.DbUpgradeService;
import com.zrlog.common.Constants;
import com.zrlog.common.ZrLogConfig;
import com.zrlog.common.vo.Version;
import com.zrlog.install.business.response.LastVersionInfo;
import com.zrlog.install.web.InstallAction;
import com.zrlog.install.web.config.DefaultInstallConfig;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.ZrLogUtil;

import java.io.File;
import java.util.Objects;

public class ZrLogInstallConfig extends DefaultInstallConfig {

    private final ZrLogConfig zrLogConfig;
    private final File dbPropertiesFile;
    private final File lockFile;

    public ZrLogInstallConfig(ZrLogConfig zrLogConfig, File dbPropertiesFile, File lockFile) {
        this.zrLogConfig = zrLogConfig;
        this.dbPropertiesFile = dbPropertiesFile;
        this.lockFile = lockFile;
    }

    @Override
    public InstallAction getAction() {
        return new ZrLogInstallAction(zrLogConfig, lockFile);
    }

    @Override
    public boolean isWarMode() {
        return ZrLogUtil.isWarMode();
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
        UpdateVersionInfoPlugin plugin = zrLogConfig.getPlugin(UpdateVersionInfoPlugin.class);
        if (Objects.isNull(plugin)) {
            return new LastVersionInfo();
        }
        Version lastVersion = plugin.getLastVersion(true);
        boolean upgradable = ZrLogUtil.greatThenCurrentVersion(lastVersion.getBuildId(), lastVersion.getBuildDate(), lastVersion.getVersion());
        LastVersionInfo lastVersionInfo = new LastVersionInfo();
        lastVersionInfo.setLatestVersion(upgradable);
        if (upgradable) {
            lastVersionInfo.setNewVersion(lastVersion.getVersion());
        }
        return lastVersionInfo;
    }
}
