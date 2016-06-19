package com.fzb.blog.util;

import com.fzb.common.util.CmdUtil;
import org.apache.log4j.Logger;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.*;

/**
 * Created by xiaochun on 2016/2/11.
 */
public class PluginConfig {

    private static Logger LOGGER = Logger.getLogger(PluginConfig.class);

    private static Set<InputStream> inSet = new HashSet<InputStream>();
    private static Process pr;
    private static boolean canStart = true;

    public static int pluginServerStart(final File serverFileName, final String dbProperties, final String pluginJvmArgs) {
        final int randomServerPort = new Random().nextInt(10000) + 20000;
        final int randomMasterPort = randomServerPort + 20000;
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        final int pid = Integer.valueOf(runtimeMXBean.getName().split("@")[0]);
        try {
            registerHook();
            if (serverFileName.getName().endsWith(".jar")) {
                new Thread() {
                    @Override
                    public void run() {
                        while (true) {
                            pr = CmdUtil.getProcess("java " + pluginJvmArgs + " -jar " + serverFileName.toString() + " " +
                                    randomServerPort + " " + randomMasterPort + " " + dbProperties + " " + serverFileName.getParent() + "/jars" + " " + pid);
                            if (pr != null) {
                                printInputStreamWithThread(pr.getInputStream());
                                inSet.add(pr.getInputStream());
                                printInputStreamWithThread(pr.getErrorStream());
                                inSet.add(pr.getErrorStream());
                            }
                            while (inSet.size() > 0) {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {

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
                                    inSet.remove(in);
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

    private static void registerHook() {
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
        if (inSet.size() > 0) {
            for (InputStream in : inSet) {
                try {
                    in.close();
                } catch (IOException e) {
                    LOGGER.warn("close process error");
                }
            }
            inSet.clear();
        }
        if (pr != null) {
            pr.destroy();
        }
        LOGGER.info("close plugin ");
    }
}
