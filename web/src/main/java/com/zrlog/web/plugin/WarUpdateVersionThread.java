package com.zrlog.web.plugin;

import com.hibegin.common.util.ExceptionUtils;
import com.hibegin.common.util.FileUtils;
import com.hibegin.common.util.JarPackageUtil;
import com.hibegin.common.util.ZipUtil;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.zrlog.common.Constants;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 更新Zrlog，具体的流程看 run() 里面有详细流程。本质就是合成新war包，然后替换掉war包。
 */
public class WarUpdateVersionThread extends Thread implements Serializable, UpdateVersionHandler {

    private static final Logger LOGGER = Logger.getLogger(WarUpdateVersionThread.class);

    private File file;
    private StringBuilder sb = new StringBuilder();
    private boolean finish;
    private File tempFilePath;

    public WarUpdateVersionThread(File file) {
        this.file = file;
        tempFilePath = new File(System.getProperty("java.io.tmpdir") + File.separator + UUID.randomUUID().toString());
        tempFilePath.mkdirs();
    }

    private void updateProcessMsg(String str) {
        LOGGER.info(str);
        sb.append("<p>").append(str).append("</p>");
    }

    private void updateProcessErrorMsg(Throwable e) {
        LOGGER.error(e);
        sb.append("<pre style='color:red'>").append(ExceptionUtils.recordStackTraceMsg(e)).append("</pre>");
    }

    @Override
    public void run() {
        try {
            String warName = getWarNameAndBackup();
            File upgradeWarFile = generatorUpgradeWarFile(warName);
            doUpgrade(warName, upgradeWarFile);
        } catch (Exception e) {
            LOGGER.error("", e);
            updateProcessErrorMsg(e);
        } finally {
            FileUtils.deleteFile(tempFilePath.toString());
        }
        finish = true;
    }

    private void doUpgrade(String warName, File upgradeWarFile) {
        File currentRunWarFile = new File(new File(PathKit.getWebRootPath()).getParentFile() + warName);
        currentRunWarFile.delete();
        FileUtils.moveOrCopyFile(upgradeWarFile.toString(), currentRunWarFile.toString(), true);
        updateProcessMsg("覆盖更新包 " + currentRunWarFile);
    }


    private File generatorUpgradeWarFile(String warName) throws IOException {
        ZipUtil.unZip(file.toString(), tempFilePath + File.separator);
        updateProcessMsg("解压安装包 " + file);
        Map<String, String> copyFileMap = new LinkedHashMap<>();
        copyFileMap.put(PathKit.getWebRootPath() + "/WEB-INF/db.properties", tempFilePath + "/WEB-INF/");
        copyFileMap.put(PathKit.getWebRootPath() + "/WEB-INF/install.lock", tempFilePath + "/WEB-INF/");
        copyFileMap.put(PathKit.getWebRootPath() + Constants.ATTACHED_FOLDER, tempFilePath.toString());
        copyFileMap.put(PathKit.getWebRootPath() + "/favicon.ico", tempFilePath.toString());
        copyFileMap.put(PathKit.getWebRootPath() + "/error", tempFilePath.toString());
        fillTemplateCopyInfo(tempFilePath, copyFileMap);
        doCopy(copyFileMap);
        List<File> fileList = new ArrayList<>();
        fileList.add(tempFilePath);
        File tempWarFile = new File(tempFilePath.getParent() + File.separator + warName);
        JarPackageUtil.inJar(fileList, tempFilePath + File.separator, tempWarFile.toString());
        updateProcessMsg("合成更新包 " + tempWarFile);
        return tempWarFile;
    }

    private void fillTemplateCopyInfo(File tempFilePath, Map<String, String> copyFileMap) {
        File templatePath = new File(PathKit.getWebRootPath() + Constants.TEMPLATE_BASE_PATH);
        File[] templates = templatePath.listFiles();
        if (templates != null && templates.length > 0) {
            for (File template : templates) {
                if (template.isDirectory() && template.toString().substring(PathKit.getWebRootPath().length()).startsWith(Constants.DEFAULT_TEMPLATE_PATH)) {
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

    private String getWarNameAndBackup() {
        String warName;
        String contextPath = JFinal.me().getServletContext().getContextPath();
        if ("/".equals(contextPath) || "".equals(contextPath)) {
            warName = "/ROOT.war";
        } else {
            warName = contextPath + ".war";
        }
        String backupFolder = new File(PathKit.getWebRootPath()).getParentFile().getParentFile() + File.separator + "backup" + File.separator + new SimpleDateFormat("yyyy-MM-dd_HH_mm").format(new Date()) + File.separator;
        new File(backupFolder).mkdirs();
        FileUtils.moveOrCopyFolder(PathKit.getWebRootPath(), backupFolder, false);
        String warPath = new File(PathKit.getWebRootPath()).getParent() + File.separator + warName;
        if (new File(warPath).exists()) {
            FileUtils.moveOrCopyFolder(warPath, backupFolder, false);
        }
        updateProcessMsg("备份当前版本到 " + backupFolder);
        return warName;
    }

    /**
     * 提示更新进度
     *
     * @return
     */
    @Override
    public String getMessage() {
        return sb.toString();
    }

    @Override
    public boolean isFinish() {
        return finish;
    }

}
