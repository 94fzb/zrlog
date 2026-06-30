package com.zrlog.web.setup.install;

import com.zrlog.business.version.UpgradeVersionHandler;
import com.zrlog.common.Constants;
import com.zrlog.common.Updater;
import com.zrlog.common.UpdaterTypeEnum;
import com.zrlog.common.vo.Version;
import com.zrlog.install.business.response.LastVersionInfo;
import org.junit.Test;

import java.io.File;
import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class ZrLogInstallConfigTest {

    @Test
    public void shouldExposeInstalledConfigDefaultsWithoutVersionPrefetch() throws Exception {
        TestUpdater updater = new TestUpdater(UpdaterTypeEnum.ZIP);
        TestZrLogConfig config = TestZrLogConfig.installedConfig(updater);
        File dbPropertiesFile = File.createTempFile("zrlog-db", ".properties");
        File installLockFile = File.createTempFile("zrlog-install", ".lock");

        ZrLogInstallConfig installConfig = new ZrLogInstallConfig(config, dbPropertiesFile, installLockFile, updater);

        assertSame(dbPropertiesFile, installConfig.getDbPropertiesFile());
        assertSame(installLockFile, installConfig.getAction().getLockFile());
        assertFalse(installConfig.isWarMode());
        assertNull(installConfig.getLastVersionInfo());
        assertEquals(Constants.DEFAULT_TEMPLATE_PATH, installConfig.defaultTemplatePath());
        assertEquals(String.valueOf(UpgradeVersionHandler.SQL_VERSION), installConfig.getZrLogSqlVersion());
        assertEquals(Constants.MYSQL_JDBC_PARAMS,
                installConfig.getJdbcUrlQueryStr("mysql", Collections.emptyMap()));
        assertEquals("", installConfig.getJdbcUrlQueryStr("postgresql", Collections.emptyMap()));
    }

    @Test
    public void shouldDetectWarUpdaterMode() throws Exception {
        TestZrLogConfig config = TestZrLogConfig.installedConfig(new TestUpdater(UpdaterTypeEnum.WAR));

        ZrLogInstallConfig installConfig = new ZrLogInstallConfig(config,
                File.createTempFile("zrlog-db", ".properties"),
                File.createTempFile("zrlog-install", ".lock"),
                new TestUpdater(UpdaterTypeEnum.WAR));

        assertTrue(installConfig.isWarMode());
    }

    @Test
    public void shouldDelegateAskAndMissingConfigFlags() throws Exception {
        TestZrLogConfig config = TestZrLogConfig.installedConfig(new TestUpdater(UpdaterTypeEnum.ZIP));
        config.setAskConfig(true);
        config.setMissingConfig(true);

        ZrLogInstallConfig installConfig = new ZrLogInstallConfig(config,
                File.createTempFile("zrlog-db", ".properties"),
                File.createTempFile("zrlog-install", ".lock"),
                null);

        assertTrue(installConfig.isAskConfig());
        assertTrue(installConfig.isMissingConfig());
    }

    @Test
    public void shouldTreatMissingRemoteVersionAsLatest() throws Exception {
        LastVersionInfo info = lastVersionInfo(null, null);

        assertTrue(info.getLatestVersion());
        assertNull(info.getNewVersion());
        assertNull(info.getDownloadUrl());
    }

    @Test
    public void shouldUseZipDownloadUrlForUpgradableZipPackage() throws Exception {
        Version version = upgradableVersion();

        LastVersionInfo info = lastVersionInfo(new TestUpdater(UpdaterTypeEnum.ZIP), version);

        assertFalse(info.getLatestVersion());
        assertEquals("999.0.0", info.getNewVersion());
        assertEquals("https://example.com/zrlog.zip", info.getDownloadUrl());
        assertEquals("changes", info.getChangeLog());
    }

    @Test
    public void shouldUseWarDownloadUrlForUpgradableWarPackage() throws Exception {
        Version version = upgradableVersion();

        LastVersionInfo info = lastVersionInfo(new TestUpdater(UpdaterTypeEnum.WAR), version);

        assertFalse(info.getLatestVersion());
        assertEquals("https://example.com/zrlog.war", info.getDownloadUrl());
    }

    private static LastVersionInfo lastVersionInfo(Updater updater, Version version) throws Exception {
        return ZrLogInstallConfig.toLastVersionInfo(updater, version);
    }

    private static Version upgradableVersion() {
        Version version = new Version();
        version.setBuildId("future-build");
        version.setBuildDate(new Date(System.currentTimeMillis() + 86400000L));
        version.setVersion("999.0.0");
        version.setZipDownloadUrl("https://example.com/zrlog.zip");
        version.setWarDownloadUrl("https://example.com/zrlog.war");
        version.setChangeLog("changes");
        return version;
    }
}
