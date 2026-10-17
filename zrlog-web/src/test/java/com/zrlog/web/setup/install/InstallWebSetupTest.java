package com.zrlog.web.setup.install;

import com.hibegin.common.util.LoggerUtil;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.zrlog.common.UpdaterTypeEnum;
import com.zrlog.install.business.response.InstallApiResponses;
import com.zrlog.install.exception.InvalidInstallRequestException;
import com.zrlog.install.web.InstallConstants;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void shouldKeepIncompleteInstallationNoticeFreeOfFilesystemPaths() throws Exception {
        TestUpdater updater = new TestUpdater(UpdaterTypeEnum.ZIP);
        TestZrLogConfig config = TestZrLogConfig.notInstalledConfig(updater);
        File dbPropertiesFile = File.createTempFile("zrlog-db", ".properties");
        File installLockFile = new File(dbPropertiesFile.getParentFile(), "private-install.lock");
        Logger logger = LoggerUtil.getLogger(InstallWebSetup.class);
        AtomicReference<String> message = new AtomicReference<>();
        Handler handler = new Handler() {
            @Override
            public void publish(LogRecord record) {
                message.set(record.getMessage());
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() {
            }
        };
        logger.addHandler(handler);
        try {
            new InstallWebSetup(config, dbPropertiesFile, installLockFile, updater).setup();
        } finally {
            logger.removeHandler(handler);
        }

        assertNotNull(message.get());
        assertTrue(message.get().contains("/install"));
        assertFalse(message.get().contains(installLockFile.getAbsolutePath()));
    }

    @Test
    public void shouldUseInstallErrorContractOnlyForInstallApiRoutes() throws Exception {
        TestUpdater updater = new TestUpdater(UpdaterTypeEnum.ZIP);
        TestZrLogConfig config = TestZrLogConfig.notInstalledConfig(updater);
        Map<Integer, AtomicReference<Throwable>> fallbackThrowables = new HashMap<>();
        for (int status : new int[]{400, 403, 500}) {
            AtomicReference<Throwable> fallbackThrowable = new AtomicReference<>();
            fallbackThrowables.put(status, fallbackThrowable);
            config.getServerConfig().addErrorHandle(status,
                    (request, response, throwable) -> fallbackThrowable.set(throwable));
        }
        File dbPropertiesFile = File.createTempFile("zrlog-db", ".properties");
        File installLockFile = new File(dbPropertiesFile.getParentFile(), "install.lock");

        new InstallWebSetup(config, dbPropertiesFile, installLockFile, updater).setup();

        for (int status : new int[]{400, 403, 500}) {
            AtomicReference<Object> rendered = new AtomicReference<>();
            String installUri = status == 400 ? "/api/install" : "/api/install/testDbConn";
            config.getServerConfig().getErrorHandle(status).doHandle(
                    request(installUri), response(rendered),
                    new InvalidInstallRequestException("Invalid installation request"));
            InstallApiResponses.Error error = (InstallApiResponses.Error) rendered.get();
            assertEquals(Integer.valueOf(9025), error.getError());
            assertEquals("INVALID_INSTALL_REQUEST", error.getCode());

            IllegalStateException fallbackError = new IllegalStateException("other API failure");
            config.getServerConfig().getErrorHandle(status).doHandle(
                    request("/api/installx"), response(new AtomicReference<>()), fallbackError);
            assertSame(fallbackError, fallbackThrowables.get(status).get());
        }
    }

    private static HttpRequest request(String uri) {
        return (HttpRequest) Proxy.newProxyInstance(
                InstallWebSetupTest.class.getClassLoader(), new Class[]{HttpRequest.class},
                (proxy, method, args) -> "getUri".equals(method.getName()) ? uri : null);
    }

    private static HttpResponse response(AtomicReference<Object> rendered) {
        return (HttpResponse) Proxy.newProxyInstance(
                InstallWebSetupTest.class.getClassLoader(), new Class[]{HttpResponse.class},
                (proxy, method, args) -> {
                    if ("renderJson".equals(method.getName())) {
                        rendered.set(args[0]);
                    }
                    return null;
                });
    }
}
