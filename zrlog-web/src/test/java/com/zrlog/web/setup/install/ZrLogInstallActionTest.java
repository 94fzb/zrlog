package com.zrlog.web.setup.install;

import com.hibegin.common.util.EnvKit;
import com.zrlog.common.UpdaterTypeEnum;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class ZrLogInstallActionTest {

    @Test
    public void shouldExposeInstallStateAndLockFile() throws Exception {
        TestZrLogConfig config = TestZrLogConfig.installedConfig(new TestUpdater(UpdaterTypeEnum.ZIP));
        File installLockFile = File.createTempFile("zrlog-install", ".lock");

        ZrLogInstallAction action = new ZrLogInstallAction(config, installLockFile);

        assertTrue(action.isInstalled());
        assertSame(installLockFile, action.getLockFile());
    }

    @Test
    public void shouldConfigureDatabaseAndStartPluginsAfterInstallSuccess() throws Exception {
        TestZrLogConfig config = TestZrLogConfig.installedConfig(new TestUpdater(UpdaterTypeEnum.ZIP));
        ZrLogInstallAction action = new ZrLogInstallAction(config, File.createTempFile("zrlog-install", ".lock"));

        action.installSuccess();

        assertEquals(1, config.getConfigDatabaseCalls());
        assertEquals(1, config.getStartPluginsCalls());
        assertEquals(Boolean.valueOf(!EnvKit.isFaaSMode()), config.getLastStartPluginsAsync());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldWrapStartupFailureAfterInstallSuccess() throws Exception {
        TestZrLogConfig config = TestZrLogConfig.installedConfig(new TestUpdater(UpdaterTypeEnum.ZIP));
        config.setFailConfigDatabase(true);

        new ZrLogInstallAction(config, File.createTempFile("zrlog-install", ".lock")).installSuccess();
    }
}
