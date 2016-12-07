package com.fzb.blog.web.plugin;

import com.fzb.blog.common.Constants;
import com.fzb.blog.model.WebSite;
import com.fzb.blog.util.BlogBuildInfoUtil;
import com.fzb.blog.web.plugin.type.AutoUpgradeVersionType;
import com.fzb.common.util.http.HttpUtil;
import com.jfinal.plugin.IPlugin;
import flexjson.JSONDeserializer;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class UpdateVersionPlugin implements IPlugin {

    private static final Logger LOGGER = Logger.getLogger(UpdateVersionPlugin.class);
    private Timer timer;
    private Version lastVersion;

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
}
