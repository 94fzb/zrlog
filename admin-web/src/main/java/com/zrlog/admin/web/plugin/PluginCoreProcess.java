package com.zrlog.admin.web.plugin;

import com.hibegin.common.util.CmdUtil;
import com.hibegin.common.util.LoggerUtil;
import com.hibegin.common.util.http.HttpUtil;
import com.hibegin.common.util.http.handle.HttpFileHandle;
import com.zrlog.common.Constants;

import java.io.*;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PluginCoreProcess {

    private static final Logger LOGGER = LoggerUtil.getLogger(PluginCoreProcess.class);

    /**
     * 插件服务的下载地址
     */
    private static final String PLUGIN_CORE_DOWNLOAD_URL = Constants.ZRLOG_RESOURCE_DOWNLOAD_URL + "/plugin/core/plugin-core.jar";

    private Process pr;
    private boolean canStart = true;
    private static final PluginCoreProcess instance = new PluginCoreProcess();

    private PluginCoreProcess() {
    }

    public static PluginCoreProcess getInstance() {
        return instance;
    }

    /**
     * plugin-core 目前会监听2个TCP端口，一个用于和Zrlog通信，另一个用于和插件通信
     *
     * @param pluginCoreFile plugin-core.jar 存放的物理路径
     * @param dbProperties
     * @param pluginJvmArgs
     * @param runtimePath    plugin-core的运行目录
     * @param runTimeVersion zrlog目前的版本
     * @return
     */
    public int pluginServerStart(final File pluginCoreFile, final String dbProperties, final String pluginJvmArgs,
                                 final String runtimePath, final String runTimeVersion) {
        canStart = true;
        //简单处理，为了能在一个服务器上面启动多个ZrLog程序，使用Random端口的方式，（感兴趣可以算算概率）
        final int randomServerPort = new Random().nextInt(10000) + 20000;
        final int randomMasterPort = randomServerPort + 20000;
        final int randomListenPort = randomServerPort + 30000;
        try {
            registerShutdownHook();
            if (pluginCoreFile.getName().endsWith(".jar")) {
                new Thread() {
                    @Override
                    public void run() {
                        while (true) {
                            tryDownloadPluginCoreFile(pluginCoreFile);
                            String javaHome = System.getProperty("java.home").replace("\\", "/");
                            String java = javaHome + "/bin/java";
                            if (java.contains(" ")) {
                                java = "java";
                            }
                            pr = CmdUtil.getProcess(java, pluginJvmArgs, "-Duser.dir=" + pluginCoreFile.getParent(), "-jar", pluginCoreFile, randomServerPort,
                                    randomMasterPort, dbProperties, pluginCoreFile.getParent() + "/jars", randomListenPort, runtimePath, runTimeVersion);
                            PluginGhostThread pluginGhostThread = new PluginGhostThread("127.0.0.1", randomListenPort);
                            if (pr != null) {
                                printInputStreamWithThread(pr.getInputStream(), pluginCoreFile, pluginGhostThread);
                                printInputStreamWithThread(pr.getErrorStream(), pluginCoreFile, pluginGhostThread);
                            }
                            try {
                                //待插件启动
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                //ignore
                            }
                            pluginGhostThread.start();
                            while (!pluginGhostThread.isStop()) {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    LOGGER.log(Level.SEVERE, "", e);
                                }
                            }
                            if (!canStart) {
                                break;
                            }
                        }
                    }

                    private void printInputStreamWithThread(final InputStream in, final File serverFileName, final PluginGhostThread pluginGhostThread) {
                        new Thread(() -> {
                            BufferedReader br = new BufferedReader(new InputStreamReader(in));
                            String str;
                            try {
                                str = br.readLine();
                                if (str != null && str.startsWith("Error: Invalid or corrupt jarfile")) {
                                    System.out.println(str);
                                    serverFileName.delete();
                                    pluginGhostThread.setStop(true);
                                }
                                while ((str = br.readLine()) != null) {
                                    System.out.println(str);
                                }
                            } catch (IOException e) {
                                LOGGER.log(Level.SEVERE, "plugin output error", e);
                            }
                        }).start();
                    }
                }.start();
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "start plugin exception ", e);
        }
        return randomServerPort;

    }

    private void tryDownloadPluginCoreFile(File serverFileName) {
        if (!serverFileName.exists()) {
            serverFileName.getParentFile().mkdirs();
            String filePath = serverFileName.getParentFile().toString();
            try {
                LOGGER.info("plugin-core.jar not exists will download from " + PLUGIN_CORE_DOWNLOAD_URL);
                HttpUtil.getInstance().sendGetRequest(PLUGIN_CORE_DOWNLOAD_URL + "?_=" + System.currentTimeMillis(),
                        new HashMap<>(), new HttpFileHandle(filePath), Map.of("Cache-Control", "no-cache"));
            } catch (IOException | URISyntaxException | InterruptedException e) {
                LOGGER.log(Level.WARNING, "download plugin core error", e);
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

    public void stopPluginCore() {
        if (pr != null) {
            pr.destroy();
        }
        canStart = false;
    }
}
