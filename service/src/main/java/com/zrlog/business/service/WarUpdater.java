package com.zrlog.business.service;

import com.hibegin.common.util.FileUtils;
import com.hibegin.common.util.LoggerUtil;
import com.hibegin.common.util.ZipUtil;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.business.util.JarPackageUtil;
import com.zrlog.common.Constants;
import com.zrlog.common.Updater;
import com.zrlog.common.UpdaterTypeEnum;
import com.zrlog.common.vo.Version;

import java.io.File;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

public class WarUpdater implements Updater {
    private static final Logger LOGGER = LoggerUtil.getLogger(WarUpdater.class);
    private final File warFilePath;

    public WarUpdater(File warFilePath) {
        this.warFilePath = warFilePath;
    }

    private static Map<String, String> getCopyFileMap(File tempFilePath) {
        Map<String, String> copyFileMap = new LinkedHashMap<>();
        copyFileMap.put(PathUtil.getRootPath() + "/WEB-INF/db.properties", tempFilePath + "/WEB-INF/");
        copyFileMap.put(PathUtil.getRootPath() + "/WEB-INF/install.lock", tempFilePath + "/WEB-INF/");
        copyFileMap.put(PathUtil.getRootPath() + "/WEB-INF/conf.properties", tempFilePath + "/WEB-INF/");
        copyFileMap.put(PathUtil.getRootPath() + Constants.ATTACHED_FOLDER, tempFilePath.toString());
        copyFileMap.put(PathUtil.getRootPath() + "/favicon.ico", tempFilePath.toString());
        copyFileMap.put(PathUtil.getRootPath() + "/error", tempFilePath.toString());
        return copyFileMap;
    }

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
    public String backup() {
        String backupFolder = new File(PathUtil.getRootPath()).getParentFile().getParentFile() + File.separator + "backup" + File.separator + new SimpleDateFormat("yyyy-MM-dd_HH_mm").format(new Date()) + File.separator;
        new File(backupFolder).mkdirs();
        FileUtils.moveOrCopyFolder(PathUtil.getRootPath(), backupFolder, false);
        String warPath = new File(PathUtil.getRootPath()).getParent() + File.separator + warFilePath.getName();
        if (new File(warPath).exists()) {
            FileUtils.moveOrCopyFolder(warPath, backupFolder, false);
        }
        //return warName;
        return backupFolder;
    }

    private void deregister() {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            // 只 deregister 当前 Web 应用加载的 JDBC 驱动
            if (driver.getClass().getClassLoader() == this.getClass().getClassLoader()) {
                try {
                    DriverManager.deregisterDriver(driver);
                    LOGGER.info("Deregistered JDBC driver: " + driver);
                } catch (SQLException e) {
                    LOGGER.severe(e.getMessage());
                }
            }
        }
    }

    @Override
    public void restartProcessAsync(Version upgradeVersion) {
        Constants.zrLogConfig.stop();
        deregister();
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
    public UpdaterTypeEnum getType() {
        return UpdaterTypeEnum.WAR;
    }

    @Override
    public String buildUpgradeFile(String upgradeFile, String upgradeKey) {
        try {
            File tempFilePath = new File(PathUtil.getTempPath() + "/" + upgradeKey);
            ZipUtil.unZip(upgradeFile, tempFilePath + File.separator);
            Map<String, String> copyFileMap = getCopyFileMap(tempFilePath);
            fillTemplateCopyInfo(tempFilePath, copyFileMap);
            doCopy(copyFileMap);
            JarPackageUtil.inJar(new ArrayList<>(List.of(tempFilePath)), tempFilePath + File.separator, warFilePath.toString());
            FileUtils.deleteFile(tempFilePath.toString());
            return warFilePath.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
