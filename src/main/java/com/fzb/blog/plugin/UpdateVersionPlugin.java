package com.fzb.blog.plugin;

import com.fzb.blog.model.WebSite;
import com.fzb.blog.plugin.type.AutoUpgradeVersionType;
import com.fzb.common.util.http.HttpUtil;
import com.jfinal.plugin.IPlugin;

import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class UpdateVersionPlugin implements IPlugin {

    private Timer timer;

    @Override
    public boolean start() {
        String value = WebSite.dao.getValueByName("autoUpgradeVersion");
        if (value != null && !"".equals(value)) {
            AutoUpgradeVersionType autoUpgradeVersionType = AutoUpgradeVersionType.valueOf(value);
            if (timer != null) {
                timer.cancel();
            }
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        String txtContent = HttpUtil.getTextByUrl("http://dl.zrlog.com/release/last.version.txt");
                        if (!"".equals(txtContent)) {

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
        return true;
    }
}
