package com.zrlog.web.plugin;

import com.hibegin.common.util.http.HttpUtil;
import com.jfinal.plugin.IPlugin;
import com.zrlog.common.Constants;
import com.zrlog.common.type.AutoUpgradeVersionType;
import com.zrlog.common.vo.Version;
import com.zrlog.model.WebSite;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.I18nUtil;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 基于JFinal插件的实现，使用定时器，定时检查是否有新的版本发布。
 */
public class UpdateVersionPlugin implements IPlugin {

    private static final Logger LOGGER = Logger.getLogger(UpdateVersionPlugin.class);

    private ScheduledExecutorService scheduledExecutorService;

    private UpdateVersionTimerTask updateVersionTimerTask;

    private void initExecutorService() {
        scheduledExecutorService = new ScheduledThreadPoolExecutor(1, r -> {
            Thread thread = new Thread(r);
            thread.setName("update-version-plugin-thread");
            return thread;
        });
    }

    @Override
    public boolean start() {
        String value = new WebSite().getStringValueByName(Constants.AUTO_UPGRADE_VERSION_KEY);
        boolean checkPreview = previewAble();
        if (value != null && !"".equals(value)) {
            AutoUpgradeVersionType autoUpgradeVersionType = AutoUpgradeVersionType.cycle(Integer.parseInt(value));
            if (scheduledExecutorService != null) {
                scheduledExecutorService.shutdown();
            }
            //开启了定时检查，定时器开始工作
            if (autoUpgradeVersionType != AutoUpgradeVersionType.NEVER) {
                initExecutorService();
                updateVersionTimerTask = new UpdateVersionTimerTask(checkPreview);
                scheduledExecutorService.schedule(updateVersionTimerTask, autoUpgradeVersionType.getCycle(), TimeUnit.SECONDS);
            }
            LOGGER.info("UpdateVersionPlugin start autoUpgradeVersionType " + autoUpgradeVersionType);
        }
        return true;
    }

    @Override
    public boolean stop() {
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdown();
        }
        return true;
    }

    private boolean previewAble() {
        return new WebSite().getBoolValueByName("upgradePreview");
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


    public static String getChangeLog(String version, String buildId) {
        try {
            return HttpUtil.getInstance().getSuccessTextByUrl("http://www.zrlog.com/changelog/" +
                    version + "-" + buildId + ".html?lang=" +
                    I18nUtil.getCurrentLocale() + "&v=" + BlogBuildInfoUtil.getBuildId());
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        return "";
    }
}
