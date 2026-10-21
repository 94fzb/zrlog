package com.zrlog.web.config;

import com.hibegin.http.server.SimpleWebServer;
import com.hibegin.http.server.WebServerBuilder;
import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import com.zrlog.common.Updater;
import com.zrlog.common.ZrLogConfig;
import com.zrlog.plugin.Plugins;
import com.zrlog.web.inteceptor.DefaultInterceptor;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class ZrLogConfigImpl extends ZrLogConfig {

    private static final String MYSQL_CLEANUP_THREAD_CLASS_NAME = "com.mysql.cj.jdbc.AbandonedConnectionCleanupThread";
    private final Object stopMonitor = new Object();
    private boolean stopped;
    private WebServerBuilder serverBuilder;

    public ZrLogConfigImpl(Integer port, Updater updater, String contextPath) {
        super(port, updater, contextPath);
        new SetupConfig(this, dbPropertiesFile, installLockFile, contextPath, webSetups, updater);
        initializeWebSetups();
        serverConfig.getInterceptors().add(DefaultInterceptor.class);
    }

    @Override
    public Plugins getBasePluginList() {
        return new Plugins();
    }

    public void setServerBuilder(WebServerBuilder serverBuilder) {
        this.serverBuilder = serverBuilder;
    }

    private boolean isMySqlCleanupThreadAvailable() {
        try {
            Class.forName(MYSQL_CLEANUP_THREAD_CLASS_NAME, false, ZrLogConfigImpl.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException | LinkageError e) {
            return false;
        }
    }

    private void shutdownMySqlCleanupThread() {
        if (Objects.nonNull(this.dataSource) && this.dataSource.isWebApi()) {
            return;
        }
        if (!isMySqlCleanupThreadAvailable()) {
            return;
        }
        try {
            AbandonedConnectionCleanupThread.checkedShutdown();
        } catch (LinkageError e) {
            LOGGER.log(Level.FINE, "MySQL cleanup thread is unavailable", e);
        }
    }

    private void destroyWebServer() {
        if (Objects.isNull(serverBuilder)) {
            return;
        }
        SimpleWebServer webServer = serverBuilder.getWebServer();
        if (Objects.isNull(webServer)) {
            return;
        }
        webServer.destroy("by stop");
    }

    private void stopAcceptingRequests() {
        if (serverBuilder != null && serverBuilder.getWebServer() != null) {
            serverBuilder.getWebServer().stopAccepting("by stop");
        }
    }

    private static ExecutorService executorService(Executor executor) {
        return executor instanceof ExecutorService ? (ExecutorService) executor : null;
    }

    private static void awaitExecutor(ExecutorService executor, long deadline) throws InterruptedException {
        if (executor != null) {
            executor.awaitTermination(Math.max(0, deadline - System.nanoTime()), TimeUnit.NANOSECONDS);
        }
    }

    private void stopRequestExecutors() {
        ExecutorService decode = executorService(serverConfig.getDecodeExecutor());
        ExecutorService request = executorService(serverConfig.getRequestExecutor());
        long started = System.nanoTime();
        long gracefulDeadline = started + TimeUnit.SECONDS.toNanos(5);
        long finalDeadline = started + TimeUnit.SECONDS.toNanos(10);
        boolean interrupted = Thread.interrupted();
        try {
            if (decode != null) {
                decode.shutdown();
            }
            // Decoders can still submit requests, so drain them before closing
            // the request executor. Keep plugins and the database alive meanwhile.
            if (!interrupted) {
                awaitExecutor(decode, gracefulDeadline);
            }
            if (request != null) {
                request.shutdown();
            }
            if (!interrupted) {
                awaitExecutor(request, gracefulDeadline);
            }
        } catch (InterruptedException e) {
            interrupted = true;
        } finally {
            ExecutorService[] executors = {decode, request};
            for (ExecutorService executor : executors) {
                if (executor != null && !executor.isTerminated()) {
                    executor.shutdownNow();
                }
            }
            for (ExecutorService executor : executors) {
                if (!interrupted) {
                    try {
                        awaitExecutor(executor, finalDeadline);
                    } catch (InterruptedException e) {
                        interrupted = true;
                    }
                }
                if (executor != null && !executor.isTerminated()) {
                    LOGGER.warning("HTTP executor did not terminate during shutdown");
                }
            }
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void stop() {
        // A TERM handler and the JVM hook can overlap. Use a separate monitor
        // so in-flight requests can still enter synchronized database methods.
        synchronized (stopMonitor) {
            if (stopped) {
                return;
            }
            stopped = true;
            try {
                stopAcceptingRequests();
                stopRequestExecutors();
            } finally {
                try {
                    super.stop();
                } finally {
                    try {
                        shutdownMySqlCleanupThread();
                    } finally {
                        destroyWebServer();
                        serverConfig.getRequestCheckerExecutor().shutdownNow();
                    }
                }
            }
        }
    }
}
