package com.fzb.blog.web.plugin;

import com.fzb.common.util.CmdUtil;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Random;

/**
 * 通过调用系统命令启动Zrlog的插件服务
 */
public class PluginConfig {

    private static final Logger LOGGER = Logger.getLogger(PluginConfig.class);

    private static Process pr;
    private static boolean canStart = true;

    /**
     * plugin-core 目前会监听2个TCP端口，一个用于和Zrlog通信，另一个用于和插件通信
     *
     * @param serverFileName plugin-core.jar 存放的物理路径
     * @param dbProperties
     * @param pluginJvmArgs
     * @param runtimePath    plugin-core的运行目录
     * @param runTimeVersion zrlog目前的版本
     * @return
     */
    public static int pluginServerStart(final File serverFileName, final String dbProperties, final String pluginJvmArgs,
                                        final String runtimePath, final String runTimeVersion) {
        canStart = true;
        //简单处理，为了能在一个服务器上面启动多个Zrlog程序，使用Random端口的方式，（感兴趣可以算算概率）
        final int randomServerPort = new Random().nextInt(10000) + 20000;
        final int randomMasterPort = randomServerPort + 20000;
        final int randomListenPort = randomServerPort + 30000;
        try {
            registerShutdownHook();
            if (serverFileName.getName().endsWith(".jar")) {
                new Thread() {
                    @Override
                    public void run() {
                        while (true) {
                            String javaHome = System.getProperty("java.home").replace("\\", "/");
                            String java = javaHome + "/bin/java";
                            if (java.contains(" ")) {
                                java = "java";
                            }
                            pr = CmdUtil.getProcess(java, pluginJvmArgs, "-jar", serverFileName, randomServerPort,
                                    randomMasterPort, dbProperties, serverFileName.getParent() + "/jars", randomListenPort, runtimePath, runTimeVersion);
                            if (pr != null) {
                                printInputStreamWithThread(pr.getInputStream());
                                printInputStreamWithThread(pr.getErrorStream());
                            }
                            PluginSocketThread pluginSocketThread = new PluginSocketThread("127.0.0.1", randomListenPort);
                            pluginSocketThread.start();
                            while (!pluginSocketThread.isStop()) {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    //ignore
                                }
                            }
                            if (!canStart) {
                                break;
                            }
                        }
                    }

                    private void printInputStreamWithThread(final InputStream in) {
                        new Thread() {
                            @Override
                            public void run() {
                                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                                String str;
                                try {
                                    while ((str = br.readLine()) != null) {
                                        System.out.println(str);
                                    }
                                } catch (IOException e) {
                                    LOGGER.error("plugin output error", e);
                                }
                            }
                        }.start();
                    }
                }.start();
            }
        } catch (Exception e) {
            LOGGER.warn("start plugin exception ", e);
        }
        return randomServerPort;

    }

    /**
     * Zrlog异常终止后，停止对应的插件服务。
     */
    private static void registerShutdownHook() {
        Runtime rt = Runtime.getRuntime();
        rt.addShutdownHook(new Thread() {
            @Override
            public void run() {
                stopPluginCore();
            }
        });
    }

    public static void stopPluginCore() {
        if (pr != null) {
            pr.destroy();
        }
        canStart = false;
    }
}
