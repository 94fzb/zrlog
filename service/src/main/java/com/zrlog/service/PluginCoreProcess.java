package com.zrlog.service;

import com.hibegin.common.util.CmdUtil;
import com.hibegin.common.util.http.HttpUtil;
import com.hibegin.common.util.http.handle.HttpFileHandle;
import com.zrlog.common.Constants;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.Random;

public class PluginCoreProcess {

    private static final Logger LOGGER = Logger.getLogger(PluginCoreProcess.class);

    /**
     * 插件服务的下载地址
     */
    private static final String PLUGIN_CORE_DOWNLOAD_URL = Constants.ZRLOG_RESOURCE_DOWNLOAD_URL + "/plugin/core/plugin-core.jar";

    private Process pr;
    private boolean canStart = true;
    private static PluginCoreProcess instance = new PluginCoreProcess();

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
        //简单处理，为了能在一个服务器上面启动多个Zrlog程序，使用Random端口的方式，（感兴趣可以算算概率）
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
                            PluginSocketThread pluginSocketThread = new PluginSocketThread("127.0.0.1", randomListenPort);
                            if (pr != null) {
                                printInputStreamWithThread(pr.getInputStream(), pluginCoreFile, pluginSocketThread);
                                printInputStreamWithThread(pr.getErrorStream(), pluginCoreFile, pluginSocketThread);
                            }
                            try {
                                //待插件启动
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                //ignore
                            }
                            pluginSocketThread.start();
                            while (!pluginSocketThread.isStop()) {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    LOGGER.error("", e);
                                }
                            }
                            if (!canStart) {
                                break;
                            }
                        }
                    }

                    private void printInputStreamWithThread(final InputStream in, final File serverFileName, final PluginSocketThread pluginSocketThread) {
                        new Thread(() -> {
                            BufferedReader br = new BufferedReader(new InputStreamReader(in));
                            String str;
                            try {
                                str = br.readLine();
                                if (str != null && str.startsWith("Error: Invalid or corrupt jarfile")) {
                                    System.out.println(str);
                                    serverFileName.delete();
                                    pluginSocketThread.setStop(true);
                                }
                                while ((str = br.readLine()) != null) {
                                    System.out.println(str);
                                }
                            } catch (IOException e) {
                                LOGGER.error("plugin output error", e);
                            }
                        }).start();
                    }
                }.start();
            }
        } catch (Exception e) {
            LOGGER.warn("start plugin exception ", e);
        }
        return randomServerPort;

    }

    private void tryDownloadPluginCoreFile(File serverFileName) {
        if (!serverFileName.exists()) {
            serverFileName.getParentFile().mkdirs();
            String filePath = serverFileName.getParentFile().toString();
            try {
                LOGGER.info("plugin-core.jar not exists will download from " + PLUGIN_CORE_DOWNLOAD_URL);
                HttpUtil.getInstance().sendGetRequest(PLUGIN_CORE_DOWNLOAD_URL + "?_=" + System.currentTimeMillis(), new HashMap<String, String[]>(), new HttpFileHandle(filePath), new HashMap<String, String>());
            } catch (IOException e) {
                LOGGER.warn("download plugin core error", e);
            }
        }
    }

    /**
     * Zrlog异常终止后，停止对应的插件服务。
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
