package com.zrlog.web.config;

import com.hibegin.http.server.WebServerBuilder;
import com.zrlog.common.CacheService;
import com.zrlog.common.TokenService;
import com.zrlog.web.inteceptor.DefaultInterceptor;
import com.zrlog.web.support.InstalledTestSite;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class ZrLogConfigImplTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private static void restoreProperty(String key, String value) {
        if (value == null) {
            System.clearProperty(key);
        } else {
            System.setProperty(key, value);
        }
    }

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
    public void shouldCreateSharedPluginsInTheServiceModuleAfterInstallation() throws Exception {
        withInstalledDatabaseRuntime(rootPath -> {
            ZrLogConfigImpl config = new ZrLogConfigImpl(19084, null, "/blog");
            try {
                System.clearProperty("env");
                assertTrue(config.getBasePluginList().isEmpty());
                var plugins = config.getWebSetup(com.zrlog.business.web.ServiceWebSetup.class).getPlugins();
                assertEquals(2, plugins.size());
                assertTrue(plugins.stream().anyMatch(com.zrlog.business.plugin.CacheManagerPlugin.class::isInstance));
                assertTrue(plugins.stream().anyMatch(com.zrlog.business.plugin.PluginCorePluginImpl.class::isInstance));
            } finally {
                config.stop();
            }
        });
    }

    @Test
    public void shouldConfigureInstalledDatabaseAndTokenServiceFromH2() throws Exception {
        withInstalledDatabaseRuntime(rootPath -> {
            ZrLogConfigImpl config = new ZrLogConfigImpl(19085, null, "");
            try {
                assertTrue(config.isInstalled());
                assertTrue(new File(rootPath, "conf/install.lock").isFile());
                assertEquals("Installed H2 Site", config.getServerConfig().getBindings().build().get(CacheService.class).getPublicWebSiteInfo().getTitle());
                assertTrue(config.getDataSource() != null);
                assertTrue(config.getServerConfig().getBindings().build().get(CacheService.class) != null);
                assertEquals(Long.valueOf(3600L), config.getServerConfig().getBindings().build().get(CacheService.class).getPublicWebSiteInfo().getSession_timeout());

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

    @Test(timeout = 15000)
    public void shouldDrainDecodedRequestsBeforeClosingDatabaseAndCompleteOverlappingStops() throws Exception {
        withInstalledDatabaseRuntime(rootPath -> {
            ZrLogConfigImpl config = new ZrLogConfigImpl(0, null, "");
            ExecutorService decode = (ExecutorService) config.getServerConfig().getDecodeExecutor();
            ExecutorService requests = (ExecutorService) config.getServerConfig().getRequestExecutor();
            CountDownLatch decoding = new CountDownLatch(1);
            CountDownLatch finishDecode = new CountDownLatch(1);
            CompletableFuture<Integer> result = new CompletableFuture<>();
            Thread first = new Thread(config::stop);
            CountDownLatch secondStarted = new CountDownLatch(1);
            CountDownLatch secondStopped = new CountDownLatch(1);
            Thread second = new Thread(() -> {
                secondStarted.countDown();
                config.stop();
                secondStopped.countDown();
            });
            try {
                decode.execute(() -> {
                    decoding.countDown();
                    try {
                        finishDecode.await();
                        requests.execute(() -> {
                            try (var connection = config.configDatabase().getConnection();
                                 var statement = connection.createStatement();
                                 var rows = statement.executeQuery("SELECT 1")) {
                                rows.next();
                                result.complete(rows.getInt(1));
                            } catch (Exception e) {
                                result.completeExceptionally(e);
                            }
                        });
                    } catch (Exception e) {
                        result.completeExceptionally(e);
                    }
                });
                assertTrue(decoding.await(2, TimeUnit.SECONDS));
                first.start();
                long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(2);
                while (!decode.isShutdown() && System.nanoTime() < deadline) {
                    Thread.sleep(5);
                }
                assertTrue(decode.isShutdown());
                assertFalse(requests.isShutdown());
                second.start();
                assertTrue(secondStarted.await(2, TimeUnit.SECONDS));
                assertFalse(secondStopped.await(100, TimeUnit.MILLISECONDS));
                finishDecode.countDown();

                assertEquals(Integer.valueOf(1), result.get(3, TimeUnit.SECONDS));
                first.join(3000);
                second.join(3000);
                assertFalse(first.isAlive());
                assertFalse(second.isAlive());
                assertTrue(decode.isTerminated());
                assertTrue(requests.isTerminated());
                assertTrue(config.getServerConfig().getRequestCheckerExecutor().isShutdown());
                config.stop();
            } finally {
                finishDecode.countDown();
                first.join(3000);
                second.join(3000);
                config.stop();
            }
        });
    }

    @Test(timeout = 10000)
    public void shouldCancelExecutorWorkAndPreserveInterruptedStop() throws Exception {
        withTestRuntime(() -> {
            ZrLogConfigImpl config = new ZrLogConfigImpl(0, null, "");
            ExecutorService requests = (ExecutorService) config.getServerConfig().getRequestExecutor();
            CountDownLatch running = new CountDownLatch(1);
            CountDownLatch cancelled = new CountDownLatch(1);
            AtomicBoolean interruptPreserved = new AtomicBoolean();
            requests.execute(() -> {
                running.countDown();
                try {
                    new CountDownLatch(1).await();
                } catch (InterruptedException e) {
                    cancelled.countDown();
                    Thread.currentThread().interrupt();
                }
            });
            Thread stop = new Thread(() -> {
                Thread.currentThread().interrupt();
                config.stop();
                interruptPreserved.set(Thread.currentThread().isInterrupted());
            });
            try {
                assertTrue(running.await(2, TimeUnit.SECONDS));
                stop.start();
                stop.join(3000);

                assertFalse(stop.isAlive());
                assertTrue(interruptPreserved.get());
                assertTrue(cancelled.await(2, TimeUnit.SECONDS));
                assertTrue(requests.awaitTermination(2, TimeUnit.SECONDS));
            } finally {
                requests.shutdownNow();
                stop.join(3000);
                config.stop();
            }
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
        }
    }

    private void withInstalledDatabaseRuntime(ThrowingRuntimeRunnable runnable) throws Exception {
        String previousRootPath = System.getProperty("sws.root.path");
        String previousEnv = System.getProperty("env");
        File rootPath = temporaryFolder.newFolder("zrlog-web-installed");
        try (InstalledTestSite ignored = InstalledTestSite.open(rootPath)) {
            System.setProperty("sws.root.path", rootPath.getAbsolutePath());
            System.setProperty("env", "junit-test");
            runnable.run(rootPath);
        } finally {
            restoreProperty("env", previousEnv);
            restoreProperty("sws.root.path", previousRootPath);
        }
    }

    private interface ThrowingRunnable {
        void run() throws Exception;
    }

    private interface ThrowingRuntimeRunnable {
        void run(File rootPath) throws Exception;
    }

}
