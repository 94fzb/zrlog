package com.zrlog.web.plugin;

import com.zrlog.common.Constants;
import com.zrlog.util.BlogBuildInfoUtil;
import com.google.gson.Gson;
import com.hibegin.common.util.http.HttpUtil;
import org.apache.commons.lang3.StringUtils;
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

    public UpdateVersionTimerTask(boolean checkPreview) {
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

    public Version fetchLastVersion(boolean ckPreview) throws IOException, ParseException {
        String versionUrl;
        if (ckPreview) {
            versionUrl = Constants.ZRLOG_RESOURCE_DOWNLOAD_URL + "/preview/last.version.json";
        } else {
            versionUrl = Constants.ZRLOG_RESOURCE_DOWNLOAD_URL + "/release/last.version.json";
        }
        String txtContent = HttpUtil.getInstance().getTextByUrl(versionUrl + "?_" + System.currentTimeMillis()).trim();
        Version tLastVersion = new Gson().fromJson(txtContent, Version.class);
        LOGGER.info(txtContent);
        //手动设置对应ChangeLog。
        String changeLogHtml = HttpUtil.getInstance().getSuccessTextByUrl("http://www.zrlog.com/changelog/" + tLastVersion.getVersion() + "-" + tLastVersion.getBuildId() + ".html");
        if (StringUtils.isNotBlank(changeLogHtml)) {
            tLastVersion.setChangeLog(changeLogHtml);
        } else {
            String commitCompareLink = "https://git.oschina.net/94fzb/zrlog/compare/" + BlogBuildInfoUtil.getBuildId() + "..." + tLastVersion.getBuildId();
            tLastVersion.setChangeLog("<h4>Not found change log,Please see commit: <a target='_blank' href='" + commitCompareLink + "'>" + commitCompareLink + "</a></h4>");
        }
        Date buildDate = new SimpleDateFormat("yyyy-MM-dd hh:mm").parse(tLastVersion.getReleaseDate());
        if (!tLastVersion.getBuildId().equals(BlogBuildInfoUtil.getBuildId()) && buildDate.after(BlogBuildInfoUtil.getTime())) {
            LOGGER.info("ZrLog New update found new [" + tLastVersion.getVersion() + "-" + tLastVersion.getBuildId() + "]");
            if (BlogBuildInfoUtil.isDev()) {
                LOGGER.info("Maybe need clone again from git repo");
            }
            this.version = tLastVersion;
            //不包含时区信息
            if (version.getReleaseDate().contains("+")) {
                version.setReleaseDate(version.getReleaseDate().substring(0, version.getReleaseDate().lastIndexOf("+")));
            }
            return version;
        }
        return null;
    }

    public Version getVersion() {
        return version;
    }
}
