package com.zrlog.business.plugin;

import com.hibegin.common.util.EnvKit;
import com.hibegin.common.util.LoggerUtil;
import com.hibegin.common.util.ZipUtil;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.business.util.NativeUtils;
import com.zrlog.business.util.PluginCoreUtils;
import com.zrlog.common.Constants;
import com.zrlog.util.ThreadUtils;
import com.zrlog.util.ZrLogUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PluginCoreProcessImpl implements PluginCoreProcess {

    private static final Logger LOGGER = LoggerUtil.getLogger(PluginCoreProcessImpl.class);

    private AbstractPluginCoreProcessHandle pluginCoreProcessHandle;
    private final AtomicBoolean stopped = new AtomicBoolean(false);
    private final File infoLogFile;
    private final File errorLogFile;
    private final Runnable onStopRunnable;
    //插件服务存放的物理路径
    private final File pluginsFolder;
    private boolean unzipped = false;


    private File getLogFile(boolean error) {
        File logFile = new File(PathUtil.getLogPath() + "/plugin-core-" + (error ? "error" : "info") + "." + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".log");
        if (logFile.exists()) {
            return logFile;
        }
        try {
            logFile.getParentFile().mkdirs();
            logFile.createNewFile();
        } catch (IOException e) {
            LOGGER.warning("Can't create plugin log file " + logFile.getName());
        }
        return logFile;
    }

    public PluginCoreProcessImpl(Runnable onStopRunnable) {
        infoLogFile = getLogFile(false);
        errorLogFile = getLogFile(true);
        this.onStopRunnable = onStopRunnable;
        this.pluginsFolder = PathUtil.getConfFile("/plugins/");
    }

    private String programName(File pluginCoreFile) {
        if (EnvKit.isNativeImage()) {
            return pluginCoreFile.toString();
        }
        String java = System.getProperty("java.home");
        if (Objects.nonNull(java)) {
            return java.replace("\\", "/") + "/bin/java";
        }
        return "java";
    }

    private void prepareUnzipPlugins() {
        if (unzipped) {
            return;
        }
        if (!EnvKit.isFaaSMode()) {
            return;
        }
        if (EnvKit.isLambda()) {
            try {
                String zipFile = ZrLogUtil.getLambdaRoot() + "/conf/plugins.zip";
                ZipUtil.unZip(zipFile, PathUtil.getConfPath());
            } catch (IOException e) {
                LOGGER.warning("Can't unzip " + ZrLogUtil.getLambdaRoot());
            } finally {
                unzipped = true;
            }
        } else {
            LOGGER.warning("Not implement ");
        }
    }

    private Process startPluginCore(final File pluginCoreFile, final String dbProperties, final String pluginJvmArgs,
                                    final String runtimePath, final String runTimeVersion, String token, int randomServerPort, int randomWatcherListenPort) throws IOException {
        final int randomMasterPort = randomServerPort + 20000;
        if (!pluginCoreFile.exists() || pluginCoreFile.length() <= 0) {
            LOGGER.warning("Missing plugin-core file " + pluginCoreFile.getName());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                //ignore
            }
            return null;
        }
        List<String> args = new ArrayList<>();
        if (!EnvKit.isNativeImage()) {
            args.addAll(Arrays.asList(pluginJvmArgs.split(" ")));
            args.add("-jar");
            args.add(pluginCoreFile.toString());
        }
        //args start
        args.add(randomServerPort + "");
        args.add(randomMasterPort + "");
        args.add(dbProperties);

        args.add(getInstalledPluginFolder());
        args.add(randomWatcherListenPort + "");
        args.add(runtimePath);
        args.add(runTimeVersion);
        args.add(Constants.zrLogConfig.getServerConfig().getPort() + "");
        args.add(token);
        if (EnvKit.isNativeImage()) {
            args.add(NativeUtils.getRealFileArch());
        } else {
            args.add("-");
        }
        //参数位置顺序需要固定
        args.add("-Duser.dir=" + pluginCoreFile.getParent());
        //args end
        List<String> cmd = new ArrayList<>();
        cmd.add(programName(pluginCoreFile));
        cmd.addAll(args);
        return new ProcessBuilder(cmd)
                .redirectOutput(infoLogFile)
                .redirectError(errorLogFile)
                .start();
    }

    private String getInstalledPluginFolder() {
        return pluginsFolder + "/installed-plugins";
    }


    @Override
    public int pluginServerStart(final String dbProperties, final String pluginJvmArgs,
                                 final String runtimePath, final String runTimeVersion, String token) {
        stopped.set(false);
        prepareUnzipPlugins();
        //简单处理，为了能在一个服务器上面启动多个ZrLog程序，使用Random端口的方式，（感兴趣可以算算概率）
        final int randomServerPort = new Random().nextInt(10000) + 20000;
        final int randomWatcherListenPort = randomServerPort + 30000;
        try {
            File coreFile = PluginCoreUtils.tryDownloadPluginCoreFile(pluginsFolder.toString());
            Process pr = startPluginCore(coreFile, dbProperties, pluginJvmArgs, runtimePath, runTimeVersion, token, randomServerPort, randomWatcherListenPort);
            registerShutdownHook();
            if (Objects.nonNull(pluginCoreProcessHandle)) {
                pluginCoreProcessHandle.close();
            }
            pluginCoreProcessHandle = new AbstractPluginCoreProcessHandle() {

                private Process process = pr;

                public void run() {
                    Thread.currentThread().setName("plugin-core-thread");

                    while (!Thread.currentThread().isInterrupted() && !stopped.get()) {
                        if (Objects.isNull(process)) {
                            return;
                        }
                        PluginConsole errroPluginConsole = new PluginConsole(errorLogFile, pluginsFolder, true);
                        PluginConsole infoPluginConsole = new PluginConsole(infoLogFile, pluginsFolder, false);
                        try {
                            infoPluginConsole.printAsync();
                            errroPluginConsole.printAsync();
                            new PluginGhostWatcher("127.0.0.1", randomWatcherListenPort).doWatch();
                            //避免执行失败的情况下，重复执行
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                //ignore
                            }
                            process = startPluginCore(coreFile, dbProperties, pluginJvmArgs, runtimePath, runTimeVersion, token, randomServerPort, randomWatcherListenPort);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        } finally {
                            try {
                                errroPluginConsole.close();
                            } catch (Exception e) {
                                LOGGER.warning("Close error stream " + e.getMessage());
                            }
                            try {
                                infoPluginConsole.close();
                            } catch (Exception e) {
                                LOGGER.warning("Close info stream " + e.getMessage());
                            }
                        }
                    }
                }

                public void close() {
                    if (Objects.nonNull(process)) {
                        process.destroy();
                        process = null;
                    }
                }
            };
            Thread thread = ThreadUtils.start(pluginCoreProcessHandle);
            thread.setUncaughtExceptionHandler((t, ex) -> pluginCoreProcessHandle.close());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "start plugin exception ", e);
        }
        return randomServerPort;

    }

    /**
     * ZrLog异常终止后，停止对应的插件服务。
     */
    private void registerShutdownHook() {
        Runtime rt = Runtime.getRuntime();
        rt.addShutdownHook(new Thread(this::stopPluginCore));
    }

    @Override
    public void stopPluginCore() {
        if (stopped.compareAndSet(false, true)) {
            if (Objects.nonNull(pluginCoreProcessHandle)) {
                pluginCoreProcessHandle.close();
            }
            if (Objects.nonNull(onStopRunnable)) {
                onStopRunnable.run();
            }
        }
    }
}
