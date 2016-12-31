package com.fzb.blog.web.plugin;

import com.fzb.blog.common.Constants;
import com.fzb.common.util.IOUtil;
import com.fzb.common.util.ZipUtil;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import org.apache.log4j.Logger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 更新Zrlog，具体的流程看 run() 里面有详细流程。本质就是合成新war包，然后替换掉war包。
 */
public class UpdateVersionThread extends Thread {

    private static final Logger LOGGER = Logger.getLogger(UpdateVersionThread.class);

    private File file;
    private StringBuilder sb = new StringBuilder();

    public UpdateVersionThread(File file) {
        this.file = file;
    }

    private void updateProcessMsg(String str) {
        sb.append("<p>").append(str).append("</p>");
    }

    public void run() {
        try {
            updateProcessMsg("开始更新");
            String warName;
            String contextPath = JFinal.me().getServletContext().getContextPath();
            String folderName;
            if (contextPath.equals("/") || contextPath.equals("")) {
                warName = "/ROOT.war";
                folderName = "ROOT/";
            } else {
                warName = contextPath + ".war";
                folderName = contextPath;
            }
            String filePath = System.getProperty("java.io.tmpdir") + "/" + UUID.randomUUID().toString();
            String backupFolder = new File(PathKit.getWebRootPath()).getParentFile().getParentFile() + "/backup/" + new SimpleDateFormat("yyyy-MM-dd_HH:mm").format(new Date()) + "/";
            updateProcessMsg("历史版本备份在 " + backupFolder);
            new File(backupFolder).mkdir();
            IOUtil.moveOrCopy(new File(PathKit.getWebRootPath()).getParent() + "/" + folderName, backupFolder, false);
            IOUtil.moveOrCopy(new File(PathKit.getWebRootPath()).getParent() + "/" + warName, backupFolder, false);
            updateProcessMsg("生成临时文件");
            File tempFilePath = new File(filePath);
            tempFilePath.mkdir();
            IOUtil.moveOrCopy(file.toString(), tempFilePath.getParentFile().toString(), false);
            String tempFile = tempFilePath.getParentFile() + "/" + file.getName();
            updateProcessMsg("解压文件");
            ZipUtil.unZip(tempFile, filePath + "/");
            updateProcessMsg("解压完成");
            List<File> fileList = new ArrayList<File>();
            fileList.add(new File(filePath));
            File tempWarFile = new File(tempFilePath.getParent() + "/" + warName);
            LOGGER.info(tempWarFile);
            IOUtil.moveOrCopy(PathKit.getWebRootPath() + "/WEB-INF/db.properties", filePath + "/WEB-INF/", false);
            IOUtil.moveOrCopy(PathKit.getWebRootPath() + "/WEB-INF/install.lock", filePath + "/WEB-INF/", false);
            File templatePath = new File(PathKit.getWebRootPath() + Constants.TEMPLATE_BASE_PATH);
            File[] templates = templatePath.listFiles();
            if (templates != null && templates.length > 0) {
                for (File template : templates) {
                    if (template.isDirectory() && template.toString().substring(PathKit.getWebRootPath().length()).equals(Constants.DEFAULT_TEMPLATE_PATH)) {
                        LOGGER.info("skip default template folder");
                        continue;
                    }
                    IOUtil.moveOrCopy(template.toString(), filePath + "/" + Constants.TEMPLATE_BASE_PATH, false);
                }
            }
            if (new File(PathKit.getWebRootPath() + "/attached").exists()) {
                IOUtil.moveOrCopy(PathKit.getWebRootPath() + "/attached", filePath, false);
            }
            ZipUtil.inZip(fileList, filePath + "/", tempWarFile.toString());
            File finalFile = new File(new File(PathKit.getWebRootPath()).getParentFile() + warName);
            updateProcessMsg("覆盖安装包文件");
            finalFile.delete();
            LOGGER.info("finalFile " + finalFile);
            IOUtil.moveOrCopy(tempWarFile.toString(), finalFile.getParentFile().toString(), false);
            updateProcessMsg("升级完成");
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(e);
            updateProcessMsg("升级失败\n" + e.getMessage());
        }
    }

    /**
     * 提示更新进度
     * @return
     */
    public String getMessage() {
        return sb.toString();
    }
}
