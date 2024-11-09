package com.zrlog.admin.web.plugin;

import com.hibegin.common.util.CmdUtil;
import com.hibegin.common.util.LoggerUtil;
import com.hibegin.common.util.http.HttpUtil;
import com.hibegin.common.util.http.handle.HttpFileHandle;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.common.Constants;
import com.zrlog.common.PluginCoreProcess;
import com.zrlog.common.type.RunMode;
import com.zrlog.util.BlogBuildInfoUtil;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PluginCoreProcessImpl implements PluginCoreProcess {

    private static final Logger LOGGER = LoggerUtil.getLogger(PluginCoreProcessImpl.class);

    private AbstractPluginCoreProcessHandle pluginCoreProcessHandle;
    private boolean canStart = true;
    private final File infoLogFile;
    private final File errorLogFile;

    public PluginCoreProcessImpl(int port) {
        infoLogFile = new File(PathUtil.getLogPath() + "/plugin-core-info." + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "." + port + ".log");
        if (infoLogFile.exists()) {
            infoLogFile.delete();
        }
        errorLogFile = new File(PathUtil.getLogPath() + "/plugin-core-error." + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "." + port + ".log");
        if (errorLogFile.exists()) {
            errorLogFile.delete();
        }
    }

    private String programName(File pluginCoreFile) {
        if (Constants.runMode == RunMode.NATIVE) {
            return pluginCoreFile.toString();
        }
        String java = System.getProperty("java.home");
        if (Objects.nonNull(java)) {
            return java.replace("\\", "/") + "/bin/java";
        }
        return "java";
    }

    private Process startPluginCore(final File pluginCoreFile, final String dbProperties, final String pluginJvmArgs,
                                    final String runtimePath, final String runTimeVersion, String token, int randomServerPort, int randomWatcherListenPort) throws IOException {
        final int randomMasterPort = randomServerPort + 20000;

        tryDownloadPluginCoreFile(pluginCoreFile);
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
        if (Constants.runMode == RunMode.DEV || Constants.runMode == RunMode.JAR) {
            args.addAll(Arrays.asList(pluginJvmArgs.split(" ")));
            args.add("-jar");
            args.add(pluginCoreFile.toString());
        }
        //args start
        args.add(randomServerPort + "");
        args.add(randomMasterPort + "");
        args.add(dbProperties);
        args.add(pluginCoreFile.getParent() + "/installed-plugins");
        args.add(randomWatcherListenPort + "");
        args.add(runtimePath);
        args.add(runTimeVersion);
        args.add(Constants.zrLogConfig.getServerConfig().getPort() + "");
        args.add(token);
        if (Constants.runMode == RunMode.NATIVE) {
            args.add(Constants.getRealFileArch());
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


    @Override
    public int pluginServerStart(final File pluginCoreFile, final String dbProperties, final String pluginJvmArgs,
                                 final String runtimePath, final String runTimeVersion, String token) {
        canStart = true;
        //简单处理，为了能在一个服务器上面启动多个ZrLog程序，使用Random端口的方式，（感兴趣可以算算概率）
        final int randomServerPort = new Random().nextInt(10000) + 20000;
        final int randomWatcherListenPort = randomServerPort + 30000;
        try {
            Process pr = startPluginCore(pluginCoreFile, dbProperties, pluginJvmArgs, runtimePath, runTimeVersion, token, randomServerPort, randomWatcherListenPort);
            registerShutdownHook();
            if (Objects.nonNull(pluginCoreProcessHandle)) {
                pluginCoreProcessHandle.close();
            }
            pluginCoreProcessHandle = new AbstractPluginCoreProcessHandle() {

                private Process process = pr;

                public void run() {
                    Thread.currentThread().setName("plugin-core-thread");

                    while (!Thread.currentThread().isInterrupted() && canStart) {
                        if (Objects.isNull(process)) {
                            return;
                        }
                        PluginConsole errroPluginConsole = new PluginConsole(errorLogFile, pluginCoreFile, true);
                        PluginConsole infoPluginConsole = new PluginConsole(infoLogFile, pluginCoreFile, false);
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
                            process = startPluginCore(pluginCoreFile, dbProperties, pluginJvmArgs, runtimePath, runTimeVersion, token, randomServerPort, randomWatcherListenPort);
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
            Thread.ofVirtual().uncaughtExceptionHandler((t, ex) -> pluginCoreProcessHandle.close()).start(pluginCoreProcessHandle);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "start plugin exception ", e);
        }
        return randomServerPort;

    }

    private void tryDownloadPluginCoreFile(File pluginCoreFile) {
        if (pluginCoreFile.exists()) {
            return;
        }
        pluginCoreFile.getParentFile().mkdirs();
        String filePath = pluginCoreFile.getParentFile().toString();
        //插件服务的下载地址
        String withoutCacheDownloadUrl = BlogBuildInfoUtil.getResourceDownloadUrl() + "/plugin/core/" + pluginCoreFile.getName() + "?_t=" + System.currentTimeMillis();
        LOGGER.info(pluginCoreFile.getName() + " not exists will download from " + withoutCacheDownloadUrl);
        try {
            HttpUtil.getInstance().sendGetRequest(withoutCacheDownloadUrl,
                    new HashMap<>(), new HttpFileHandle(filePath), Map.of("Cache-Control", "no-cache"));
        } catch (IOException | URISyntaxException | InterruptedException e) {
            LOGGER.log(Level.WARNING, "download plugin core error", e);
        }
        if (pluginCoreFile.exists() && pluginCoreFile.length() > 0) {
            if (pluginCoreFile.getName().endsWith(".bin")) {
                CmdUtil.sendCmd("chmod", "a+x", pluginCoreFile.toString());
            }
        }
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
        canStart = false;
        if (Objects.nonNull(pluginCoreProcessHandle)) {
            pluginCoreProcessHandle.close();
        }
    }
}
