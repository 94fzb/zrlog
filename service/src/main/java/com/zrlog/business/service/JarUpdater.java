package com.zrlog.business.service;

import com.hibegin.common.util.LoggerUtil;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.common.Updater;
import com.zrlog.common.vo.Version;
import com.zrlog.util.ZrLogUtil;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public record JarUpdater(String[] args, File execFile) implements Updater {

    private static final Logger LOGGER = LoggerUtil.getLogger(JarUpdater.class);

    public CompletableFuture<Void> restartProcessAsync(Version upgradeVersion) {
        return CompletableFuture.runAsync(() -> {
            List<String> inputArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
            // 构造完整的命令
            List<String> cmdArgs = new ArrayList<>();
            cmdArgs.add("java");
            cmdArgs.addAll(inputArguments);
            cmdArgs.add("-jar");
            cmdArgs.add(execFile.toString());
            cmdArgs.addAll(Arrays.stream(args).toList());
            if (cmdArgs.stream().noneMatch(e -> e.startsWith("--port="))) {
                cmdArgs.add("--port=" + ZrLogUtil.getPort(args));
            }
            try {
                LOGGER.info("ZrLog file updated. exec cmd\n" + String.join(" ", cmdArgs));
                Thread.sleep(2000);
                Runtime.getRuntime().exec(cmdArgs.toArray(new String[0]));
                System.exit(0);
            } catch (Exception e) {
                LOGGER.warning("Restart error " + e.getMessage());
            }
        });
    }

    @Override
    public String getUnzipPath() {
        return PathUtil.getRootPath();
    }

}