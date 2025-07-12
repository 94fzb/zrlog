package com.zrlog.admin.util;

import com.hibegin.common.util.EnvKit;
import com.hibegin.common.util.LoggerUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SystemLoad {

    private static final Logger LOGGER = LoggerUtil.getLogger(SystemLoad.class);

    public static void main(String[] args) {
        // 调用方法获取系统负载信息
        String loadInfo = getSystemLoad();
        System.out.println("System Load: " + loadInfo);
        System.out.println("parseLine(\"11:30  up 3 days,  1:51, 4 users, load averages: 3.40 3.22 2.97\") = " + parseLine("11:30  up 3 days,  1:51, 4 users, load averages: 3.40 3.22 2.97"));
    }

    private static boolean isWindows() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("windows");
    }

    private static String parseLine(String line) {
        String loadPattern = "load averages?: ([\\d.]+),? ([\\d.]+),? ([\\d.]+)";
        Pattern pattern = Pattern.compile(loadPattern);
        Matcher matcher = pattern.matcher(line);

        if (matcher.find()) {
            double load1 = Double.parseDouble(matcher.group(1));
            double load5 = Double.parseDouble(matcher.group(2));
            double load15 = Double.parseDouble(matcher.group(3));
            // 返回负载信息
            return String.format("%.2f, %.2f, %.2f", load1, load5, load15);
        } else {
            return "---";
        }
    }

    public static String getSystemLoad() {
        if (isWindows()) {
            return "-";
        }
        if (EnvKit.isFaaSMode()) {
            return "-";
        }
        try {
            // 执行uptime命令
            ProcessBuilder processBuilder = new ProcessBuilder("uptime");
            Process process = processBuilder.start();

            // 读取命令输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            if ((line = reader.readLine()) != null) {
                return parseLine(line);
            }
            // 等待命令执行完成
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                // 如果命令执行失败，输出错误信息
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String errorLine;
                while ((errorLine = errorReader.readLine()) != null) {
                    LOGGER.warning("Exec uptime error -> " + errorLine);
                }
            }
        } catch (Exception e) {
            LOGGER.warning("Load system info error " + e.getMessage());
        }

        // 返回-表示不支持的系统
        return "-";
    }
}
