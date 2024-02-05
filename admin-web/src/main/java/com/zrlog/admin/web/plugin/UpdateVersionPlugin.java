package com.zrlog.admin.web.plugin;

import com.hibegin.common.util.LoggerUtil;
import com.hibegin.common.util.StringUtils;
import com.hibegin.common.util.http.HttpUtil;
import com.zrlog.business.service.InstallService;
import com.zrlog.common.Constants;
import com.zrlog.common.type.AutoUpgradeVersionType;
import com.zrlog.common.vo.Version;
import com.zrlog.model.WebSite;
import com.zrlog.plugin.IPlugin;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.I18nUtil;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 基于插件的实现，使用定时器，定时检查是否有新的版本发布。
 */
public class UpdateVersionPlugin implements IPlugin {

    private static final Logger LOGGER = LoggerUtil.getLogger(UpdateVersionPlugin.class);

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
        if (value != null && !value.isEmpty()) {
            AutoUpgradeVersionType autoUpgradeVersionType = AutoUpgradeVersionType.cycle((int) Double.parseDouble(value));
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
        try {
            return new WebSite().getBoolValueByName("upgradePreview");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
                LOGGER.log(Level.SEVERE, "", e);
            }
        }
        return updateVersionTimerTask.getVersion();
    }


    public static String getChangeLog(String version, String buildId) {
        try {
            String changeLog = HttpUtil.getInstance().getSuccessTextByUrl("https://www.zrlog.com/changelog/" +
                    version + "-" + buildId + ".html?lang=" +
                    I18nUtil.getCurrentLocale() + "&v=" + BlogBuildInfoUtil.getBuildId());
            if (StringUtils.isNotEmpty(changeLog)) {
                return changeLog;
            }
        } catch (IOException | InterruptedException | URISyntaxException e) {
            LOGGER.log(Level.SEVERE, "", e);
        }
        String changeUrl = "https://github.com/94fzb/zrlog/compare/" + BlogBuildInfoUtil.getBuildId() + "..." + buildId;
        return InstallService.renderMd("#### Not find changeLog, Please view git diff \n[" + changeUrl + "](" + changeUrl + ")");
    }
}
