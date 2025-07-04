package com.zrlog.web;

import com.hibegin.common.util.EnvKit;
import com.hibegin.common.util.ParseArgsUtil;
import com.hibegin.common.util.Pid;
import com.hibegin.common.util.StringUtils;
import com.hibegin.http.server.WebServerBuilder;
import com.hibegin.lambda.LambdaApplication;
import com.zrlog.business.service.NativeImageUpdater;
import com.zrlog.common.Constants;
import com.zrlog.common.type.RunMode;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.ZrLogUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Objects;

/**
 * graalvm 实际的启动入口，开发阶段不使用这个类进行启动
 */
public class GraalvmNativeImageApplication {


    static {
        if (EnvKit.isLambda()) {
            LambdaApplication.initLambdaEnv();
        } else {
            Application.initZrLogEnv();
        }
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
            System.exit(1);
            return "zrlog.exe";
        }
    }

    private static String getExecFile() {
        if (Constants.runMode.isLambda()) {
            return "zrlog";
        }
        String envBinFile = System.getenv("_");
        if (Objects.isNull(envBinFile)) {
            return "zrlog";
        }
        if (StringUtils.isEmpty(envBinFile)) {
            envBinFile = getWindowsExecutablePath();
        }
        String execFile = envBinFile.replace("./", "");
        if (!execFile.startsWith(Constants.getZrLogHome())) {
            execFile = new File(Constants.getZrLogHome() + "/" + execFile).toString();
        }
        return execFile;
    }


    public static void main(String[] args) throws Exception {
        Constants.runMode = EnvKit.isLambda() ? RunMode.NATIVE_LAMBDA : RunMode.NATIVE;
        int port = ZrLogUtil.getPort(args);
        if (Constants.runMode.isLambda()) {
            File file = new File(ZrLogUtil.getLambdaRoot() + "/zrlog");
            WebServerBuilder webServerBuilder = Application.webServerBuilder(port, ZrLogUtil.getContextPath(args), new NativeImageUpdater(args, file));
            webServerBuilder.startInBackground();
            LambdaApplication.startHandle(Constants.zrLogConfig);
            return;
        }
        String execFile = getExecFile();
        //parse args
        if (ParseArgsUtil.justTips(args, new File(execFile).getName(), BlogBuildInfoUtil.getVersionInfoFull())) {
            return;
        }
        WebServerBuilder webServerBuilder = Application.webServerBuilder(port, ZrLogUtil.getContextPath(args), new NativeImageUpdater(args, new File(execFile)));
        webServerBuilder.start();
    }
}
