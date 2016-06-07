package com.fzb.blog.util;

import com.fzb.common.util.CmdUtil;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by xiaochun on 2016/2/11.
 */
public class PluginConfig {

    private static Logger LOGGER = Logger.getLogger(PluginConfig.class);

    private static final Map<String, Process> processMap = new HashMap<String, Process>();

    public static int pluginServerStart(final File serverFileName, final String dbProperties, final String pluginJvmArgs) {
        final int randomServerPort = new Random().nextInt(10000) + 20000;
        final int randomMasterPort = randomServerPort + 20000;
        try {
            registerHook();
            if (serverFileName.getName().endsWith(".jar")) {
                new Thread() {
                    @Override
                    public void run() {
                        Process pr = CmdUtil.getProcess("java " + pluginJvmArgs + " -jar " + serverFileName.toString() + " " +
                                randomServerPort + " " + randomMasterPort + " " + dbProperties + " " + serverFileName.getParent() + "/jars");
                        String pluginName = serverFileName.getName().replace(".jar", "");
                        if (pr != null) {
                            processMap.put(pluginName, pr);
                            printInputStreamWithThread(pr.getInputStream(), pluginName);
                            printInputStreamWithThread(pr.getErrorStream(), pluginName);
                        }
                    }

                    private void printInputStreamWithThread(final InputStream in, final String pluginName) {
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

    private static void registerHook() {
        Runtime rt = Runtime.getRuntime();
        rt.addShutdownHook(new Thread() {
            @Override
            public void run() {
                stopPluginCore();
            }
        });
    }

    public static void stopPluginCore() {
        for (Map.Entry<String, Process> entry : processMap.entrySet()) {
            entry.getValue().destroyForcibly();
            LOGGER.info("close plugin " + " " + entry.getKey());
        }
    }
}
