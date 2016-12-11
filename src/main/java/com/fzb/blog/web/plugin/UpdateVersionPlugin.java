package com.fzb.blog.web.plugin;

import com.fzb.blog.common.Constants;
import com.fzb.blog.model.WebSite;
import com.fzb.blog.util.BlogBuildInfoUtil;
import com.fzb.blog.web.plugin.type.AutoUpgradeVersionType;
import com.fzb.common.util.IOUtil;
import com.fzb.common.util.ZipUtil;
import com.fzb.common.util.http.HttpUtil;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.IPlugin;
import flexjson.JSONDeserializer;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class UpdateVersionPlugin implements IPlugin {

    private static final Logger LOGGER = Logger.getLogger(UpdateVersionPlugin.class);
    private Timer timer;
    private Version lastVersion;
    private boolean updating;
    private int process;
    private StringBuffer sb = new StringBuffer();

    @Override
    public boolean start() {
        final String value = WebSite.dao.getValueByName("autoUpgradeVersion");
        final boolean checkPreview = "on".equals(WebSite.dao.getValueByName("upgradeCanPreview"));
        if (value != null && !"".equals(value)) {
            AutoUpgradeVersionType autoUpgradeVersionType = AutoUpgradeVersionType.cycle(Integer.parseInt(value));
            if (timer != null) {
                timer.cancel();
            }
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        String versionUrl;
                        if (checkPreview) {
                            versionUrl = Constants.ZRLOG_RESOURCE_DOWNLOAD_URL + "/preview/last.version.json";
                        } else {
                            versionUrl = Constants.ZRLOG_RESOURCE_DOWNLOAD_URL + "/release/last.version.json";
                        }
                        String txtContent = HttpUtil.getTextByUrl(versionUrl + "?_" + System.currentTimeMillis()).trim();
                        Version tLastVersion = new JSONDeserializer<Version>().deserialize(txtContent, Version.class);
                        System.out.println(txtContent);
                        tLastVersion.setChangeLog(HttpUtil.getTextByUrl("http://www.zrlog.com/changelog/" + tLastVersion.getVersion() + "-" + tLastVersion.getBuildId() + ".html"));
                        if (!tLastVersion.getBuildId().equals(BlogBuildInfoUtil.getBuildId())) {
                            LOGGER.info("ZrLog New update found new [" + tLastVersion.getVersion() + "-" + tLastVersion.getBuildId() + "]");
                            if (BlogBuildInfoUtil.isDev()) {
                                LOGGER.info("Maybe need clone again from git repo");
                            }
                            lastVersion = tLastVersion;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, new Date(), autoUpgradeVersionType.getCycle() * 1000);
        }
        return true;
    }

    @Override
    public boolean stop() {
        if (timer != null) {
            timer.cancel();
        }
        return true;
    }

    public Version getLastVersion() {
        return lastVersion;
    }

    public String getProcessMsg() {
        return sb.toString();
    }

    public int getProcess() {
        return process;
    }

    private void updateProcessMsg(String str) {
        sb.append("<p>").append(str).append("</p>");
    }

    public void startUpgrade(final File file) {
        updating = true;
        final UpdateVersionPlugin plugin = this;
        new Thread() {
            @Override
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
                    process = 100;
                    plugin.stop();
                } catch (Exception e) {
                    e.printStackTrace();
                    LOGGER.error(e);
                    updateProcessMsg("升级失败\n" + e.getMessage());
                }
            }
        }.start();
    }

    public boolean isUpdating() {
        return updating;
    }
}
