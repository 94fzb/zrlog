package com.zrlog.business.service;

import com.hibegin.common.util.FileUtils;
import com.hibegin.common.util.LoggerUtil;
import com.hibegin.common.util.ZipUtil;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.business.util.JarPackageUtil;
import com.zrlog.common.Constants;
import com.zrlog.common.Updater;
import com.zrlog.common.vo.Version;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

public record WarUpdater(String[] args, File warFilePath) implements Updater {

    private static final Logger LOGGER = LoggerUtil.getLogger(WarUpdater.class);

    private void fillTemplateCopyInfo(File tempFilePath, Map<String, String> copyFileMap) {
        File templatePath = new File(PathUtil.getRootPath() + Constants.TEMPLATE_BASE_PATH);
        File[] templates = templatePath.listFiles();
        if (templates != null) {
            for (File template : templates) {
                if (template.isDirectory() && template.toString().substring(PathUtil.getRootPath().length()).startsWith(Constants.DEFAULT_TEMPLATE_PATH)) {
                    //skip default template folder
                    continue;
                }
                copyFileMap.put(template.toString(), tempFilePath + File.separator + Constants.TEMPLATE_BASE_PATH);
            }
        }
    }

    private void doCopy(Map<String, String> pathMap) {
        for (Map.Entry<String, String> entry : pathMap.entrySet()) {
            if (new File(entry.getKey()).exists()) {
                FileUtils.moveOrCopyFolder(entry.getKey(), entry.getValue(), false);
            }
        }
    }


    @Override
    public void backup() {
        String backupFolder = new File(PathUtil.getRootPath()).getParentFile().getParentFile() + File.separator + "backup" + File.separator + new SimpleDateFormat("yyyy-MM-dd_HH_mm").format(new Date()) + File.separator;
        new File(backupFolder).mkdirs();
        FileUtils.moveOrCopyFolder(PathUtil.getRootPath(), backupFolder, false);
        String warPath = new File(PathUtil.getRootPath()).getParent() + File.separator + warFilePath.getName();
        if (new File(warPath).exists()) {
            FileUtils.moveOrCopyFolder(warPath, backupFolder, false);
        }
        //updateProcessMsg("备份当前版本到 " + backupFolder);
        //return warName;
    }

    @Override
    public void restartProcessAsync(Version upgradeVersion) {

    }

    @Override
    public String getUnzipPath() {
        return "";
    }

    @Override
    public File execFile() {
        return warFilePath;
    }

    @Override
    public String buildUpgradeFile() {
        try {
            File tempFilePath = new File(System.getProperty("java.io.tmpdir"));
            ZipUtil.unZip(warFilePath.toString(), tempFilePath + File.separator);
            //updateProcessMsg("解压安装包 " + file);
            Map<String, String> copyFileMap = new LinkedHashMap<>();
            copyFileMap.put(PathUtil.getRootPath() + "/WEB-INF/db.properties", tempFilePath + "/WEB-INF/");
            copyFileMap.put(PathUtil.getRootPath() + "/WEB-INF/install.lock", tempFilePath + "/WEB-INF/");
            copyFileMap.put(PathUtil.getRootPath() + Constants.ATTACHED_FOLDER, tempFilePath.toString());
            copyFileMap.put(PathUtil.getRootPath() + "/favicon.ico", tempFilePath.toString());
            copyFileMap.put(PathUtil.getRootPath() + "/error", tempFilePath.toString());
            fillTemplateCopyInfo(tempFilePath, copyFileMap);
            doCopy(copyFileMap);
            List<File> fileList = new ArrayList<>();
            fileList.add(tempFilePath);
            File tempWarFile = new File(tempFilePath.getParent() + File.separator + warFilePath.getName());
            JarPackageUtil.inJar(fileList, tempFilePath + File.separator, tempWarFile.toString());
            //updateProcessMsg("合成更新包 " + tempWarFile);
            return tempWarFile.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
