package com.zrlog.util;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public record JarUpdater(String[] args, String fileName) {

    private static final Logger LOGGER = com.hibegin.common.util.LoggerUtil.getLogger(JarUpdater.class);

    public CompletableFuture<Void> restartJarAsync() {
        return CompletableFuture.runAsync(() -> {
            LOGGER.info("ZrLog file updated. Restarting...");
            List<String> inputArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();

            // 构造完整的命令
            List<String> cmdArgs = new ArrayList<>();
            cmdArgs.add("java");
            cmdArgs.addAll(inputArguments);
            cmdArgs.add("-jar");
            cmdArgs.add(fileName);
            cmdArgs.addAll(Arrays.stream(args).toList());
            if (cmdArgs.stream().noneMatch(e -> e.startsWith("--port="))) {
                cmdArgs.add("--port=" + ZrLogUtil.getPort(args));
            }
            try {
                Thread.sleep(1000);
                LOGGER.info("Recall " + Arrays.toString(cmdArgs.toArray()));
                Runtime.getRuntime().exec(cmdArgs.toArray(new String[0]));
                System.exit(0);
            } catch (Exception e) {
                LOGGER.warning("Restart error " + e.getMessage());
            }
        });
    }

}