package com.zrlog.web;

import com.hibegin.common.util.Pid;
import com.hibegin.common.util.StringUtils;
import com.hibegin.http.server.WebServerBuilder;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.business.service.NativeImageUpdater;
import com.zrlog.common.Constants;
import com.zrlog.common.type.RunMode;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.ZrLogUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

/**
 * graalvm 实际的启动入口，开发阶段不使用这个类进行启动
 */
public class GraalvmNativeImageApplication {

    public static boolean justTips(String[] args, String processName, String version) {
        if (args.length > 0) {
            switch (args[0]) {
                case "-v", "--version" -> {
                    System.out.println(processName + " version: " + version);
                    return true;
                }
                case "--properties" -> {
                    System.getProperties().forEach((key, value) -> System.out.format("%s=%s%n",
                            key,
                            value));
                    return true;
                }
                case "--env" -> {
                    System.getenv().forEach((key, value) -> System.out.format("%s=%s%n",
                            key,
                            value));
                    return true;
                }
            }
        }
        return false;
    }

    private static String getWindowsExecutablePath() {
        try {
            Process process = Runtime.getRuntime().exec("cmd /c wmic process where processid=" + Pid.get() + " get ExecutablePath");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            reader.readLine(); // Skip the header line
            String executablePath = reader.readLine(); // The second line is the executable path
            if (StringUtils.isEmpty(executablePath)) {
                return "zrlog.exe";
            }
            return new File(executablePath.trim()).getName();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
            return "zrlog.exe";
        }
    }

    public static void main(String[] args) {
        String binFile = System.getenv("_");
        if (StringUtils.isEmpty(binFile)) {
            binFile = getWindowsExecutablePath();
        }
        String exeFile = binFile.replace("./", "");
        if (justTips(args, new File(exeFile).getName(), BlogBuildInfoUtil.getVersionInfoFull())) {
            return;
        }
        Constants.runMode = RunMode.NATIVE;
        String runtimeRoot = System.getProperty("user.dir");
        if (!exeFile.startsWith(runtimeRoot)) {
            exeFile = new File(PathUtil.getRootPath() + "/" + exeFile).toString();
        }
        PathUtil.setRootPath(runtimeRoot);
        int port = ZrLogUtil.getPort(args);
        WebServerBuilder webServerBuilder = Application.webServerBuilder(port, new NativeImageUpdater(args, exeFile));
        webServerBuilder.start();
    }
}
