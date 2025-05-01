package com.zrlog.web.util;

import com.hibegin.common.util.EnvKit;
import com.hibegin.common.util.Pid;
import com.hibegin.common.util.StringUtils;
import com.zrlog.common.Constants;
import com.zrlog.util.ZrLogUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Objects;

public class ZrLogNativeImageUtils {

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

    public static String getExecFile() {
        if (EnvKit.isFaaSMode()) {
            return ZrLogUtil.getFaaSRoot() + "/zrlog";
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
}
