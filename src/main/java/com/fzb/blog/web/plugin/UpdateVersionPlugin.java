package com.fzb.blog.web.plugin;

import com.fzb.blog.model.WebSite;
import com.fzb.blog.web.plugin.type.AutoUpgradeVersionType;
import com.jfinal.plugin.IPlugin;

import java.util.Date;
import java.util.Timer;

import static com.fzb.blog.common.Constants.AUTO_UPGRADE_VERSION_KEY;

/**
 * 基于JFinal插件的实现，使用定时器，定时检查是否有新的版本发布。
 */
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
            //开启了定时检查，定时器开始工作
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

    /**
     * 获取最新的版本信息。
     *
     * @param fetch 是否发起新的请求
     * @return
     */
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
