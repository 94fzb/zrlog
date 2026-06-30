package com.zrlog.web.config;

import com.hibegin.http.server.WebServerBuilder;
import com.zrlog.common.Constants;
import com.zrlog.common.TokenService;
import com.zrlog.web.support.InMemoryZrLogDatabase;
import com.zrlog.web.inteceptor.DefaultInterceptor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class ZrLogConfigImplTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void shouldConfigureCoreServerDefaultsWithoutInstalledDatabase() throws Exception {
        withTestRuntime(() -> {
            ZrLogConfigImpl config = new ZrLogConfigImpl(19080, null, "/blog");

            assertEquals(Integer.valueOf(19080), config.getServerConfig().getPort());
            assertEquals("/blog", config.getServerConfig().getContextPath());
            assertTrue(config.getServerConfig().getInterceptors().contains(DefaultInterceptor.class));
            assertNull(config.configDatabase());
            assertNull(config.getTokenService());
            assertTrue(config.getBasePluginList().isEmpty());
        });
    }

    @Test
    public void shouldCreateBasePluginsOutsideTestModeWithoutDatabase() throws Exception {
        withRuntime("junit-test", () -> {
            ZrLogConfigImpl config = new ZrLogConfigImpl(19084, null, "/blog");
            System.clearProperty("env");

            assertEquals(2, config.getBasePluginList().size());
        });
    }

    @Test
    public void shouldConfigureInstalledDatabaseAndTokenServiceFromH2() throws Exception {
        withInstalledDatabaseRuntime(rootPath -> {
            ZrLogConfigImpl config = new ZrLogConfigImpl(19085, null, "");
            try {
                Constants.zrLogConfig = config;
                assertTrue(config.isInstalled());
                assertTrue(config.getDataSource() != null);
                assertTrue(config.getCacheService() != null);
                assertEquals(Long.valueOf(3600L), config.getCacheService().getPublicWebSiteInfo().getSession_timeout());

                TokenService tokenService = config.getTokenService();
                assertTrue(tokenService != null);
                assertSame(tokenService, config.getTokenService());
            } finally {
                config.stop();
            }
        });
    }

    @Test
    public void shouldStopWhenServerBuilderIsMissing() throws Exception {
        withTestRuntime(() -> {
            ZrLogConfigImpl config = new ZrLogConfigImpl(19082, null, "");

            config.stop();

            assertNull(config.getTokenService());
        });
    }

    @Test
    public void shouldStopWhenWebServerIsMissing() throws Exception {
        withTestRuntime(() -> {
            ZrLogConfigImpl config = new ZrLogConfigImpl(19083, null, "");
            WebServerBuilder builder = new WebServerBuilder.Builder().config(config).build();
            config.setServerBuilder(builder);

            config.stop();

            assertTrue(builder.getWebServer() == null);
        });
    }

    private void withTestRuntime(ThrowingRunnable runnable) throws Exception {
        withRuntime("junit-test", runnable);
    }

    private void withRuntime(String env, ThrowingRunnable runnable) throws Exception {
        String previousRootPath = System.getProperty("sws.root.path");
        String previousEnv = System.getProperty("env");
        try {
            System.setProperty("sws.root.path", temporaryFolder.newFolder("zrlog-web").getAbsolutePath());
            restoreProperty("env", env);
            runnable.run();
        } finally {
            restoreProperty("env", previousEnv);
            restoreProperty("sws.root.path", previousRootPath);
            Constants.zrLogConfig = null;
        }
    }

    private void withInstalledDatabaseRuntime(ThrowingRuntimeRunnable runnable) throws Exception {
        String previousRootPath = System.getProperty("sws.root.path");
        String previousEnv = System.getProperty("env");
        File rootPath = temporaryFolder.newFolder("zrlog-web-installed");
        try (InMemoryZrLogDatabase ignored = InMemoryZrLogDatabase.open(rootPath)) {
            System.setProperty("sws.root.path", rootPath.getAbsolutePath());
            System.setProperty("env", "junit-test");
            runnable.run(rootPath);
        } finally {
            restoreProperty("env", previousEnv);
            restoreProperty("sws.root.path", previousRootPath);
            Constants.zrLogConfig = null;
        }
    }

    private static void restoreProperty(String key, String value) {
        if (value == null) {
            System.clearProperty(key);
        } else {
            System.setProperty(key, value);
        }
    }

    private interface ThrowingRunnable {
        void run() throws Exception;
    }

    private interface ThrowingRuntimeRunnable {
        void run(File rootPath) throws Exception;
    }

}
