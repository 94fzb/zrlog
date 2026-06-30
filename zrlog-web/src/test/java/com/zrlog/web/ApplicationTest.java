package com.zrlog.web;

import com.hibegin.http.server.WebServerBuilder;
import com.zrlog.common.Constants;
import com.zrlog.web.config.ZrLogConfigImpl;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ApplicationTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void shouldBuildWebServerWithoutStartingIt() throws Exception {
        withTestRuntime(() -> {
            WebServerBuilder builder = Application.webServerBuilder(19082, "/site", null);

            assertNotNull(builder);
            assertNull(builder.getWebServer());
            assertTrue(Constants.zrLogConfig instanceof ZrLogConfigImpl);
            assertEquals(Integer.valueOf(19082), Constants.zrLogConfig.getServerConfig().getPort());
            assertEquals("/site", Constants.zrLogConfig.getServerConfig().getContextPath());
            assertTrue(Constants.zrLogConfig.getServerConfig().getOnCreateErrorHandles().size() > 0);
        });
    }

    @Test
    public void shouldReturnImmediatelyForVersionStartArgument() {
        Application.start(new String[]{"--version"});

        assertNull(Constants.zrLogConfig);
    }

    @Test
    public void shouldReturnImmediatelyForVersionMainArgument() throws Exception {
        Application.main(new String[]{"--version"});

        assertNull(Constants.zrLogConfig);
    }

    @Test
    public void shouldReturnImmediatelyForNativeVersionArgument() throws Exception {
        Application.nativeStart(new String[]{"--version"});

        assertNull(Constants.zrLogConfig);
    }

    @Test
    public void shouldIgnoreMissingZrLogHomeWhenInitializingEnvironment() throws Exception {
        String previousRootPath = System.getProperty("sws.root.path");
        try {
            Application.initZrLogEnv();

            assertEquals(previousRootPath, System.getProperty("sws.root.path"));
        } finally {
            restoreProperty("sws.root.path", previousRootPath);
        }
    }

    private void withTestRuntime(ThrowingRunnable runnable) throws Exception {
        String previousRootPath = System.getProperty("sws.root.path");
        String previousEnv = System.getProperty("env");
        try {
            System.setProperty("sws.root.path", temporaryFolder.newFolder("zrlog-web-app").getAbsolutePath());
            System.setProperty("env", "junit-test");
            runnable.run();
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
}
