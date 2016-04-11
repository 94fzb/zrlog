package com.fzb.blog.plugin;

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

    public static int pluginServerStart(final File serverFileName, final String dbProperties) {
        final int randomServerPort = new Random().nextInt(10000) + 20000;
        final int randomMasterPort = randomServerPort + 20000;
        try {
            final Map<String, Process> processMap = new HashMap<String, Process>();
            registerHook(processMap);
            if (serverFileName.getName().endsWith(".jar")) {
                new Thread() {
                    @Override
                    public void run() {
                        Process pr = CmdUtil.getProcess("java -Xms64m -Xmx64m -jar " + serverFileName.toString() + " " +
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

    private static void registerHook(final Map<String, Process> processMap) {
        Runtime rt = Runtime.getRuntime();
        rt.addShutdownHook(new Thread() {
            @Override
            public void run() {
                for (Map.Entry<String, Process> entry : processMap.entrySet()) {
                    entry.getValue().destroyForcibly();
                    LOGGER.info("close plugin " + " " + entry.getKey());
                }
            }
        });
    }
}
