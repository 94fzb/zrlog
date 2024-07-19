package com.zrlog.admin.web.plugin;

import com.hibegin.common.util.LoggerUtil;
import com.hibegin.common.util.StringUtils;
import com.hibegin.common.util.http.HttpUtil;
import com.zrlog.business.service.InstallService;
import com.zrlog.common.Constants;
import com.zrlog.common.type.AutoUpgradeVersionType;
import com.zrlog.common.vo.Version;
import com.zrlog.plugin.IPlugin;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.I18nUtil;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
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
        if (isStarted()) {
            return true;
        }
        String value = (String) Constants.zrLogConfig.getWebSite().get(Constants.AUTO_UPGRADE_VERSION_KEY);
        if (value != null && !value.isEmpty()) {
            AutoUpgradeVersionType autoUpgradeVersionType = AutoUpgradeVersionType.cycle((int) Double.parseDouble(value));
            if (scheduledExecutorService != null) {
                scheduledExecutorService.shutdown();
            }
            //开启了定时检查，定时器开始工作
            if (autoUpgradeVersionType != AutoUpgradeVersionType.NEVER) {
                initExecutorService();
                updateVersionTimerTask = new UpdateVersionTimerTask();
                scheduledExecutorService.scheduleAtFixedRate(updateVersionTimerTask, 0, autoUpgradeVersionType.getCycle(), TimeUnit.SECONDS);
            }
            LOGGER.info("UpdateVersionPlugin start autoUpgradeVersionType " + autoUpgradeVersionType);
        }
        return true;
    }

    @Override
    public boolean isStarted() {
        return Objects.nonNull(scheduledExecutorService);
    }

    @Override
    public boolean stop() {
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdown();
            scheduledExecutorService = null;
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
        if (updateVersionTimerTask == null) {
            updateVersionTimerTask = new UpdateVersionTimerTask();
        }
        if (fetch) {
            try {
                updateVersionTimerTask.run();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "", e);
            }
        }
        return updateVersionTimerTask.getVersion();
    }

    private static boolean isHtml(String str) {
        return str.startsWith("<!DOCTYPE html>") || str.startsWith("<html>");
    }

    public static String getChangeLog(String version, Date releaseDate, String buildId, Map<String, Object> res) {
        try {
            String changeLogMd = HttpUtil.getInstance().getSuccessTextByUrl("https://www.zrlog.com/changelog/" +
                    version + "-" + buildId + ".md?lang=" +
                    I18nUtil.getCurrentLocale() + "&v=" + BlogBuildInfoUtil.getBuildId());
            if (StringUtils.isNotEmpty(changeLogMd) && !isHtml(changeLogMd)) {
                return InstallService.renderMd(changeLogMd);
            }
        } catch (IOException | InterruptedException | URISyntaxException e) {
            LOGGER.log(Level.SEVERE, "", e);
        }
        if (Objects.equals(BlogBuildInfoUtil.getBuildId(), buildId)) {
            return InstallService.renderMd((String) res.get("upgradeNoChange"));
        }
        String uriPath = "94fzb/zrlog/compare/" + BlogBuildInfoUtil.getBuildId() + "..." + buildId;
        String changeUrl = "https://github.com/" + uriPath;
        return InstallService.renderMd("### " + version + " (" + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(releaseDate) + ")\n" + res.get("upgradeNoChangeLog") + "\n[" + uriPath + "](" + changeUrl + ")");
    }
}
