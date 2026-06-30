package com.zrlog.web.setup.install;

import com.zrlog.common.UpdaterTypeEnum;
import com.zrlog.web.WebSetup;
import com.zrlog.web.WebSetupContext;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InstallWebSetupProviderTest {

    @Test
    public void shouldExposeInstallModuleMetadata() {
        InstallWebSetupProvider provider = new InstallWebSetupProvider();

        assertEquals("install", provider.name());
        assertEquals(200, provider.order());
    }

    @Test
    public void shouldCreateInstallWebSetupFromContext() throws Exception {
        InstallWebSetupProvider provider = new InstallWebSetupProvider();
        TestUpdater updater = new TestUpdater(UpdaterTypeEnum.ZIP);
        TestZrLogConfig config = TestZrLogConfig.installedConfig(updater);
        File dbPropertiesFile = File.createTempFile("zrlog-db", ".properties");
        File installLockFile = File.createTempFile("zrlog-install", ".lock");

        WebSetup setup = provider.create(new WebSetupContext(config, dbPropertiesFile, installLockFile, "/blog", updater));

        assertTrue(setup instanceof InstallWebSetup);
    }
}
