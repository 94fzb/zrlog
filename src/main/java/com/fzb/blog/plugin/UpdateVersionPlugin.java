package com.fzb.blog.plugin;

import com.fzb.blog.model.WebSite;
import com.fzb.blog.plugin.type.AutoUpgradeVersionType;
import com.fzb.blog.util.BlogBuildInfoUtil;
import com.fzb.common.util.http.HttpUtil;
import com.jfinal.plugin.IPlugin;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class UpdateVersionPlugin implements IPlugin {

    private static final Logger LOGGER = Logger.getLogger(UpdateVersionPlugin.class);
    private Timer timer;

    @Override
    public boolean start() {
        final String value = WebSite.dao.getValueByName("autoUpgradeVersion");
        final boolean checkPreview = "on".equals(WebSite.dao.getValueByName("upgradeCanParam"));
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
                            versionUrl = "http://dl.zrlog.com/release/last.version.txt";
                        } else {
                            versionUrl = "http://dl.zrlog.com/preview/last.version.txt";
                        }
                        String txtContent = HttpUtil.getTextByUrl(versionUrl).trim();
                        if (!"".equals(txtContent) && !BlogBuildInfoUtil.getVersion().equals(txtContent)) {
                            LOGGER.info("ZrLog New update found new [" + txtContent + "]");
                            if (BlogBuildInfoUtil.isDev()) {
                                LOGGER.info("Maybe need clone again from git repo");
                            }
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
}
