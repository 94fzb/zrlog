package com.zrlog.web.setup.install;

import com.zrlog.common.UpdaterTypeEnum;
import com.zrlog.install.web.InstallConstants;
import org.junit.After;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

public class InstallWebSetupTest {

    @After
    public void tearDown() {
        InstallConstants.installConfig = null;
    }

    @Test
    public void shouldRegisterInstallConfigAndRouterWhenSetupRuns() throws Exception {
        TestUpdater updater = new TestUpdater(UpdaterTypeEnum.ZIP);
        TestZrLogConfig config = TestZrLogConfig.installedConfig(updater);
        File dbPropertiesFile = File.createTempFile("zrlog-db", ".properties");
        File installLockFile = File.createTempFile("zrlog-install", ".lock");

        new InstallWebSetup(config, dbPropertiesFile, installLockFile, updater).setup();

        assertNotNull(InstallConstants.installConfig);
        assertSame(installLockFile, InstallConstants.installConfig.getAction().getLockFile());
    }
}
