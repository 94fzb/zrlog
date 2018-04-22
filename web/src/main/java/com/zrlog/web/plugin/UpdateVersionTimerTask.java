package com.zrlog.web.plugin;

import com.google.gson.Gson;
import com.hibegin.common.util.http.HttpUtil;
import com.zrlog.common.Constants;
import com.zrlog.common.vo.Version;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.ZrLogUtil;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

/**
 * 定时检查是否有新的更新包可用，原则比较简单，比对服务器罪行buildId和war包的构建时间（与resources/build.properties对比）
 * 注意 开发环境没有这个文件
 */
class UpdateVersionTimerTask extends TimerTask {

    private static final Logger LOGGER = Logger.getLogger(UpdateVersionTimerTask.class);

    private boolean checkPreview;
    private Version version;

    UpdateVersionTimerTask(boolean checkPreview) {
        this.checkPreview = checkPreview;
    }

    @Override
    public void run() {
        try {
            this.version = fetchLastVersion(checkPreview);
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    Version fetchLastVersion(boolean ckPreview) throws IOException, ParseException {
        String versionUrl;
        if (ckPreview) {
            versionUrl = Constants.ZRLOG_RESOURCE_DOWNLOAD_URL + "/preview/last.version.json";
        } else {
            versionUrl = Constants.ZRLOG_RESOURCE_DOWNLOAD_URL + "/release/last.version.json";
        }
        Version lastVersion = getVersion(versionUrl);
        Date buildDate = new SimpleDateFormat(Constants.DATE_FORMAT_PATTERN).parse(lastVersion.getReleaseDate());
        //如果已是最新预览版，那么尝试检查正式版本
        if (checkPreview && !ZrLogUtil.greatThenCurrentVersion(lastVersion.getBuildId(), buildDate, lastVersion.getVersion())) {
            lastVersion = getVersion(Constants.ZRLOG_RESOURCE_DOWNLOAD_URL + "/release/last.version.json");
            buildDate = new SimpleDateFormat(Constants.DATE_FORMAT_PATTERN).parse(lastVersion.getReleaseDate());
        }
        if (ZrLogUtil.greatThenCurrentVersion(lastVersion.getBuildId(), buildDate, lastVersion.getVersion())) {
            LOGGER.info("ZrLog New update found new [" + lastVersion.getVersion() + "-" + lastVersion.getBuildId() + "]");
            this.version = lastVersion;
            version.setReleaseDate(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(buildDate));
            return version;
        }
        return null;
    }

    private Version getVersion(String versionUrl) throws IOException {
        String txtContent = HttpUtil.getInstance().getTextByUrl(versionUrl + "?_" + System.currentTimeMillis() + "&v=" + BlogBuildInfoUtil.getBuildId()).trim();
        Version tLastVersion = new Gson().fromJson(txtContent, Version.class);
        //手动设置对应ChangeLog
        tLastVersion.setChangeLog(UpdateVersionPlugin.getChangeLog(tLastVersion.getVersion(), tLastVersion.getBuildId()));
        return tLastVersion;
    }

    public Version getVersion() {
        return version;
    }
}
