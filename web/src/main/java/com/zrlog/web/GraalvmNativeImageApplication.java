package com.zrlog.web;

import com.hibegin.common.util.ParseArgsUtil;
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
        String envBinFile = System.getenv("_");
        if (StringUtils.isEmpty(envBinFile)) {
            envBinFile = getWindowsExecutablePath();
        }
        String execFile = envBinFile.replace("./", "");
        Constants.runMode = RunMode.NATIVE;
        String runtimeRoot = System.getProperty("user.dir");
        PathUtil.setRootPath(runtimeRoot);
        if (!execFile.startsWith(runtimeRoot)) {
            execFile = new File(PathUtil.getRootPath() + "/" + execFile).toString();
        }
        //parse args
        if (ParseArgsUtil.justTips(args, new File(execFile).getName(), BlogBuildInfoUtil.getVersionInfoFull())) {
            return;
        }
        int port = ZrLogUtil.getPort(args);
        WebServerBuilder webServerBuilder = Application.webServerBuilder(port, new NativeImageUpdater(args, new File(execFile)));
        webServerBuilder.start();
    }
}
