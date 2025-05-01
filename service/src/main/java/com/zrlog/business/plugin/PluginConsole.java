package com.zrlog.business.plugin;

import com.hibegin.common.util.LoggerUtil;
import com.zrlog.util.ThreadUtils;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class PluginConsole implements AutoCloseable {

    private static final Logger LOGGER = LoggerUtil.getLogger(PluginConsole.class);

    private final File outputFile;
    private final File serverFileName;
    private final ScheduledExecutorService scheduler;
    private final boolean errorStream;
    private long lastFileSize = 0;

    public PluginConsole(File outputFile, File serverFileName, boolean errorStream) {
        this.outputFile = outputFile;
        this.serverFileName = serverFileName;
        this.errorStream = errorStream;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = ThreadUtils.unstarted(runnable);
            thread.setName("plugin-console-print-thread");
            return thread;
        });
    }

    public void printAsync() {
        scheduler.scheduleAtFixedRate(() -> {
            if (!outputFile.exists()) {
                try {
                    close();
                } catch (Exception e) {
                    LOGGER.warning("Error closing PluginConsole: " + e.getMessage());
                }
                return;
            }

            long currentFileSize = outputFile.length();
            if (currentFileSize <= lastFileSize) {
                return;
            }
            // 读取新增内容
            try (FileInputStream fileInputStream = new FileInputStream(outputFile)) {
                fileInputStream.skip(lastFileSize); // 跳到上次读取位置
                byte[] buffer = new byte[(int) (currentFileSize - lastFileSize)];
                int bytesRead = fileInputStream.read(buffer);
                if (bytesRead > 0) {
                    String data = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
                    if (data.startsWith("Error: Invalid or corrupt jarfile")) {
                        if (errorStream) {
                            System.err.print(data);
                        } else {
                            System.out.print(data);
                        }
                        serverFileName.delete();
                        close();
                        return;
                    }
                    if (errorStream) {
                        System.err.print(data);
                    } else {
                        System.out.print(data);
                    }
                }
            } catch (Exception e) {
                LOGGER.warning("Error reading file: " + e.getMessage());
            }
            lastFileSize = currentFileSize;
        }, 0, 1, TimeUnit.SECONDS); // 每秒检查一次文件大小变化
    }

    @Override
    public void close() throws Exception {
        if (outputFile.exists()) {
            outputFile.delete();
        }
        scheduler.shutdown(); // 停止定时器
    }
}
