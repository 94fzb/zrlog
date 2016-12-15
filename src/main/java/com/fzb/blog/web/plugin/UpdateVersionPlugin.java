package com.fzb.blog.web.plugin;

import com.fzb.blog.model.WebSite;
import com.fzb.blog.web.plugin.type.AutoUpgradeVersionType;
import com.jfinal.plugin.IPlugin;

import java.util.Date;
import java.util.Timer;

import static com.fzb.blog.common.Constants.AUTO_UPGRADE_VERSION_KEY;

public class UpdateVersionPlugin implements IPlugin {

    private Timer timer;
    private Version lastVersion;

    @Override
    public boolean start() {
        final String value = WebSite.dao.getValueByName(AUTO_UPGRADE_VERSION_KEY);
        final boolean checkPreview = "on".equals(WebSite.dao.getValueByName("upgradeCanPreview"));
        if (value != null && !"".equals(value)) {
            AutoUpgradeVersionType autoUpgradeVersionType = AutoUpgradeVersionType.cycle(Integer.parseInt(value));
            if (timer != null) {
                timer.cancel();
            }
            if (autoUpgradeVersionType != AutoUpgradeVersionType.NEVER) {
                timer = new Timer();
                UpdateVersionTimerTask timerTask = new UpdateVersionTimerTask(checkPreview);
                timer.schedule(timerTask, new Date(), autoUpgradeVersionType.getCycle() * 1000);
                lastVersion = timerTask.getVersion();
            }
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

    public Version getLastVersion(boolean fetch) {
        if (fetch) {
            final boolean checkPreview = "on".equals(WebSite.dao.getValueByName("upgradeCanPreview"));
            UpdateVersionTimerTask timerTask = new UpdateVersionTimerTask(checkPreview);
            timerTask.run();
            lastVersion = timerTask.getVersion();
        }
        return lastVersion;
    }
}
