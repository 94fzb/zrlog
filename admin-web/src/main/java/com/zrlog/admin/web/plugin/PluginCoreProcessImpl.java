package com.zrlog.admin.web.plugin;

import com.hibegin.common.util.CmdUtil;
import com.hibegin.common.util.LoggerUtil;
import com.hibegin.common.util.http.HttpUtil;
import com.hibegin.common.util.http.handle.HttpFileHandle;
import com.zrlog.common.Constants;
import com.zrlog.common.PluginCoreProcess;
import com.zrlog.common.type.RunMode;
import com.zrlog.util.BlogBuildInfoUtil;

import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PluginCoreProcessImpl implements PluginCoreProcess {

    private static final Logger LOGGER = LoggerUtil.getLogger(PluginCoreProcessImpl.class);

    private Thread thread;
    private boolean canStart = true;

    private void printInputStreamWithThread(final InputStream in, final File serverFileName) {
        Thread.ofVirtual().start(() -> {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String str;
            try {
                str = br.readLine();
                if (str != null && str.startsWith("Error: Invalid or corrupt jarfile")) {
                    System.out.println(str);
                    serverFileName.delete();
                }
                while ((str = br.readLine()) != null) {
                    System.out.println(str);
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "plugin output error", e);
            }
        });
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
                                    final String runtimePath, final String runTimeVersion, String token, int randomServerPort, int randomWatcherListenPort) {
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
            args.add(pluginJvmArgs);
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
        return CmdUtil.getProcess(programName(pluginCoreFile), args.toArray());
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
            thread = new Thread() {
                private Process process = pr;

                public void run() {
                    Thread.currentThread().setName("plugin-core-thread");

                    while (true) {
                        if (process != null) {
                            printInputStreamWithThread(process.getInputStream(), pluginCoreFile);
                            printInputStreamWithThread(process.getErrorStream(), pluginCoreFile);
                        }
                        new PluginGhostWatcher("127.0.0.1", randomWatcherListenPort).doWatch();
                        //避免执行失败的情况下，重复执行
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            //ignore
                        }
                        if (!canStart) {
                            return;
                        }
                        process = startPluginCore(pluginCoreFile, dbProperties, pluginJvmArgs, runtimePath, runTimeVersion, token, randomServerPort, randomWatcherListenPort);
                    }
                }

                @Override
                public void interrupt() {
                    if (Objects.nonNull(process)) {
                        process.destroy();
                    }
                }
            };
            thread.start();
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
        if (Objects.nonNull(thread)) {
            thread.interrupt();
        }
        canStart = false;
    }
}
