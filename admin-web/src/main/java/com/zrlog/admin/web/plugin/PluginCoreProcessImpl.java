package com.zrlog.admin.web.plugin;

import com.hibegin.common.util.CmdUtil;
import com.hibegin.common.util.LoggerUtil;
import com.hibegin.common.util.http.HttpUtil;
import com.hibegin.common.util.http.handle.HttpFileHandle;
import com.zrlog.common.Constants;
import com.zrlog.common.PluginCoreProcess;
import com.zrlog.util.BlogBuildInfoUtil;

import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PluginCoreProcessImpl implements PluginCoreProcess {

    private static final Logger LOGGER = LoggerUtil.getLogger(PluginCoreProcessImpl.class);

    private Process pr;
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


    @Override
    public int pluginServerStart(final File pluginCoreFile, final String dbProperties, final String pluginJvmArgs,
                                 final String runtimePath, final String runTimeVersion, String token) {
        if (!pluginCoreFile.getName().endsWith(".jar")) {
            return -1;
        }
        canStart = true;
        //简单处理，为了能在一个服务器上面启动多个ZrLog程序，使用Random端口的方式，（感兴趣可以算算概率）
        final int randomServerPort = new Random().nextInt(10000) + 20000;
        final int randomMasterPort = randomServerPort + 20000;
        final int randomWatcherListenPort = randomServerPort + 30000;
        try {
            registerShutdownHook();
            new Thread(() -> {
                Thread.currentThread().setName("plugin-core-thread");
                while (true) {
                    tryDownloadPluginCoreFile(pluginCoreFile);
                    String java = System.getProperty("java.home");
                    if (Objects.nonNull(java)) {
                        java = java.replace("\\", "/") + "/bin/java";
                    } else {
                        java = "java";
                    }
                    List<String> args = new ArrayList<>();
                    args.add(pluginJvmArgs);
                    args.add("-Duser.dir=" + pluginCoreFile.getParent());
                    args.add("-jar");
                    args.add(pluginCoreFile.toString());
                    //args start
                    args.add(randomServerPort + "");
                    args.add(randomMasterPort + "");
                    args.add(dbProperties);
                    args.add(pluginCoreFile.getParent() + "/jars");
                    args.add(randomWatcherListenPort + "");
                    args.add(runtimePath);
                    args.add(runTimeVersion);
                    args.add(Constants.zrLogConfig.getServerConfig().getPort() + "");
                    args.add(token);
                    //args end
                    pr = CmdUtil.getProcess(java, args.toArray());
                    if (pr != null) {
                        printInputStreamWithThread(pr.getInputStream(), pluginCoreFile);
                        printInputStreamWithThread(pr.getErrorStream(), pluginCoreFile);
                    }
                    new PluginGhostWatcher("127.0.0.1", randomWatcherListenPort).doWatch();
                    if (!canStart) {
                        return;
                    }
                }
            }).start();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "start plugin exception ", e);
        }
        return randomServerPort;

    }

    private void tryDownloadPluginCoreFile(File serverFileName) {
        if (serverFileName.exists()) {
            return;
        }
        serverFileName.getParentFile().mkdirs();
        String filePath = serverFileName.getParentFile().toString();
        //插件服务的下载地址
        String withoutCacheDownloadUrl = BlogBuildInfoUtil.getResourceDownloadUrl() + "/plugin/core/plugin-core.jar?_t=" + System.currentTimeMillis();
        LOGGER.info("plugin-core.jar not exists will download from " + withoutCacheDownloadUrl);
        try {
            HttpUtil.getInstance().sendGetRequest(withoutCacheDownloadUrl,
                    new HashMap<>(), new HttpFileHandle(filePath), Map.of("Cache-Control", "no-cache"));
        } catch (IOException | URISyntaxException | InterruptedException e) {
            LOGGER.log(Level.WARNING, "download plugin core error", e);
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
        if (pr != null) {
            pr.destroy();
        }
        canStart = false;
    }
}
