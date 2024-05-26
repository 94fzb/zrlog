package com.zrlog.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public record NativeImageUpdater(String[] args, String fileName) implements Updater {

    private static final Logger LOGGER = com.hibegin.common.util.LoggerUtil.getLogger(NativeImageUpdater.class);

    @Override
    public CompletableFuture<Void> restartJarAsync() {
        return CompletableFuture.runAsync(() -> {
            LOGGER.info("ZrLog file updated. Restarting...");
            // 构造完整的命令
            List<String> cmdArgs = new ArrayList<>();
            cmdArgs.add(fileName);
            cmdArgs.addAll(Arrays.stream(args).toList());
            if (cmdArgs.stream().noneMatch(e -> e.startsWith("--port="))) {
                cmdArgs.add("--port=" + ZrLogUtil.getPort(args));
            }
            try {
                Thread.sleep(2000);
                LOGGER.info("Recall native " + Arrays.toString(cmdArgs.toArray()));
                Runtime.getRuntime().exec(cmdArgs.toArray(new String[0]));
                System.exit(0);
            } catch (Exception e) {
                LOGGER.warning("Restart error " + e.getMessage());
            }
        });
    }
}
