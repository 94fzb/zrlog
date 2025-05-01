package com.zrlog.admin.web.plugin;

import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.LoggerUtil;
import com.hibegin.common.util.StringUtils;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.business.util.NativeUtils;
import com.zrlog.common.Constants;
import com.zrlog.common.Updater;
import com.zrlog.common.UpdaterTypeEnum;
import com.zrlog.common.vo.Version;
import com.zrlog.util.ThreadUtils;
import com.zrlog.util.ZrLogUtil;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.logging.Logger;

public class NativeImageUpdater implements Updater {

    private final String[] args;
    private final File execFile;

    private static final Logger LOGGER = LoggerUtil.getLogger(NativeImageUpdater.class);

    public NativeImageUpdater(String[] args, File execFile) {
        this.args = args;
        this.execFile = execFile;
    }

    public File execFile() {
        return execFile;
    }

    @Override
    public UpdaterTypeEnum getType() {
        return UpdaterTypeEnum.NATIVE_IMAGE;
    }

    private String buildExec() {
        StringJoiner shells = new StringJoiner("\n");
        shells.add("#!/bin/sh");
        shells.add("set -e");
        shells.add("sleep 1");
        String zipBinName = "zrlog";
        shells.add("chmod a+x " + getUpdateTempPath() + "/" + zipBinName);
        shells.add("# upgrade " + zipBinName);
        shells.add("cp -r " + getUpdateTempPath() + "/*" + " " + PathUtil.getRootPath());
        File newBinFile = new File(PathUtil.getRootPath() + "/" + zipBinName);
        //try update exec name
        if (!Objects.equals(newBinFile.toString(), execFile.toString())) {
            shells.add("mv " + newBinFile + " " + execFile);
        }
        shells.add("rm -rf " + getUpdateTempPath());
        shells.add("# start " + zipBinName);
        shells.add(buildStartExec());
        return shells.toString();
    }

    private String buildStartExec() {
        StringJoiner cmdArgs = new StringJoiner(" ");
        cmdArgs.add(execFile.toString());
        for (String arg : args) {
            if (arg.startsWith("--port=")) {
                continue;
            }
            cmdArgs.add(arg);
        }
        cmdArgs.add("--port=" + ZrLogUtil.getPort(args));
        return cmdArgs.toString();
    }

    private String buildWindowsBatExec() {
        StringJoiner shells = new StringJoiner("\n");
        String zipBinName = "zrlog.exe";
        shells.add("timeout /t 1 /nobreak > nul");
        shells.add("move " + getUpdateTempPath() + "\\*" + " " + PathUtil.getRootPath());
        File newBinFile = new File(PathUtil.getRootPath() + "\\" + zipBinName);
        //try update exec name
        if (!Objects.equals(newBinFile.toString(), execFile.toString())) {
            shells.add("move " + newBinFile + " " + execFile);
        }
        shells.add(buildStartExec());
        return shells.toString();
    }

    private String buildUpgradeCmd(Version upgradeVersion) {
        StringJoiner stringJoiner = new StringJoiner("-");
        stringJoiner.add("upgrade");
        if (Objects.nonNull(upgradeVersion) && StringUtils.isNotEmpty(upgradeVersion.getVersion())) {
            stringJoiner.add(upgradeVersion.getVersion());
        }
        if (Objects.nonNull(upgradeVersion) && StringUtils.isNotEmpty(upgradeVersion.getBuildId())) {
            stringJoiner.add(upgradeVersion.getBuildId());
        }
        stringJoiner.add(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        String fileName = stringJoiner.toString();
        if (NativeUtils.getRealFileArch().startsWith("Windows")) {
            File tempUpgradeFile = new File(PathUtil.getTempPath() + "/" + fileName + ".bat");
            IOUtil.writeStrToFile(buildWindowsBatExec(), tempUpgradeFile);
            return "cmd /c start " + tempUpgradeFile;
        }
        File tempUpgradeFile = new File(PathUtil.getTempPath() + "/" + fileName + ".sh");
        IOUtil.writeStrToFile(buildExec(), tempUpgradeFile);
        return "sh " + tempUpgradeFile + " &";

    }

    @Override
    public void restartProcessAsync(Version upgradeVersion) {
        Constants.zrLogConfig.stop();
        ThreadUtils.start(() -> {
            try {
                String cmd = buildUpgradeCmd(upgradeVersion);
                // 构造完整的命令启动
                LOGGER.info("ZrLog file updated. exec shell\n" + cmd);
                Thread.sleep(2000);
                Runtime.getRuntime().exec(cmd);
                Thread.sleep(100);
                System.exit(0);
            } catch (Exception e) {
                LOGGER.warning("Restart error " + e.getMessage());
            }
        });
    }

    @Override
    public String getUnzipPath() {
        return getUpdateTempPath().getPath();
    }
}
