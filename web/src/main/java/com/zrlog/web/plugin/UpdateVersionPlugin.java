package com.zrlog.web.plugin;

import com.jfinal.plugin.IPlugin;
import com.zrlog.common.Constants;
import com.zrlog.common.type.AutoUpgradeVersionType;
import com.zrlog.common.vo.Version;
import com.zrlog.model.WebSite;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.Timer;

/**
 * 基于JFinal插件的实现，使用定时器，定时检查是否有新的版本发布。
 */
public class UpdateVersionPlugin implements IPlugin {

    private static final Logger LOGGER = Logger.getLogger(UpdateVersionPlugin.class);

    private Timer timer;
    private UpdateVersionTimerTask updateVersionTimerTask;

    @Override
    public boolean start() {
        String value = WebSite.dao.getStringValueByName(Constants.AUTO_UPGRADE_VERSION_KEY);
        boolean checkPreview = previewAble();
        if (value != null && !"".equals(value)) {
            AutoUpgradeVersionType autoUpgradeVersionType = AutoUpgradeVersionType.cycle(Integer.parseInt(value));
            if (timer != null) {
                timer.cancel();
            }
            //开启了定时检查，定时器开始工作
            if (autoUpgradeVersionType != AutoUpgradeVersionType.NEVER) {
                timer = new Timer();
                updateVersionTimerTask = new UpdateVersionTimerTask(checkPreview);
                timer.schedule(updateVersionTimerTask, new Date(), autoUpgradeVersionType.getCycle() * 1000);
            }
            LOGGER.info("UpdateVersionPlugin start autoUpgradeVersionType " + autoUpgradeVersionType);
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

    private boolean previewAble() {
        return WebSite.dao.getBoolValueByName("upgradePreview");
    }

    /**
     * 获取最新的版本信息。
     *
     * @param fetch 是否发起新的请求
     * @return
     */
    public Version getLastVersion(boolean fetch) {
        boolean checkPreview = previewAble();
        if (updateVersionTimerTask == null) {
            updateVersionTimerTask = new UpdateVersionTimerTask(checkPreview);
        }
        if (fetch) {
            try {
                return updateVersionTimerTask.fetchLastVersion(checkPreview);
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }
        return updateVersionTimerTask.getVersion();
    }
}
