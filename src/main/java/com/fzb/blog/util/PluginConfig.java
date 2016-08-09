package com.fzb.blog.util;

import com.fzb.common.util.CmdUtil;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Random;

public class PluginConfig {

    private static final Logger LOGGER = Logger.getLogger(PluginConfig.class);

    private static Process pr;
    private static boolean canStart = true;

    public static int pluginServerStart(final File serverFileName, final String dbProperties, final String pluginJvmArgs,
                                        final String runtimePath, final String runTimeVersion) {
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
                            pr = CmdUtil.getProcess("java " + pluginJvmArgs + " -jar " + serverFileName, randomServerPort,
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

    private static void registerShutdownHook() {
        Runtime rt = Runtime.getRuntime();
        rt.addShutdownHook(new Thread() {
            @Override
            public void run() {
                stopPluginCore();
                canStart = false;
            }
        });
    }

    public static void stopPluginCore() {
        if (pr != null) {
            pr.destroy();
        }
        LOGGER.info("close plugin ");
    }
}
