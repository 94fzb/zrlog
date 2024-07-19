package com.zrlog.admin.web.plugin;

import com.google.gson.Gson;
import com.hibegin.common.util.LoggerUtil;
import com.hibegin.common.util.StringUtils;
import com.hibegin.common.util.http.HttpUtil;
import com.zrlog.common.Constants;
import com.zrlog.common.type.RunMode;
import com.zrlog.common.vo.Version;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.ZrLogUtil;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 定时检查是否有新的更新包可用，比对服务器生成最新buildId和包的构建时间（与resources/build.properties对比，注意：开发环境没有这个文件）
 */
class UpdateVersionTimerTask extends TimerTask {

    private static final Logger LOGGER = LoggerUtil.getLogger(UpdateVersionTimerTask.class);
    private Version version;

    private boolean previewAble() {
        return Constants.getBooleanByFromWebSite("upgradePreview");
    }

    @Override
    public void run() {
        try {
            Version lastVersion = fetchLastVersion(previewAble());
            //build date ok
            if (lastVersion.getBuildDate().getTime() > 0) {
                this.version = lastVersion;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "", e);
        }
    }

    private static Version fetchLastVersion(boolean ckPreview) throws IOException, ParseException, URISyntaxException, InterruptedException {
        Version lastVersion = getVersion(ckPreview);
        if (!ckPreview) {
            if (ZrLogUtil.greatThenCurrentVersion(lastVersion.getBuildId(), lastVersion.getBuildDate(), lastVersion.getVersion())) {
                LOGGER.info("ZrLog New release update found new [" + lastVersion.getVersion() + "-" + lastVersion.getBuildId() + "]");
            }
            return lastVersion;
        }
        //存在预览版本
        if (ZrLogUtil.greatThenCurrentVersion(lastVersion.getBuildId(), lastVersion.getBuildDate(), lastVersion.getVersion())) {
            LOGGER.info("ZrLog New preview update found new [" + lastVersion.getVersion() + "-" + lastVersion.getBuildId() + "]");
            return lastVersion;
        }
        //如果已是最新预览版，那么尝试检查正式版本
        Version lastReleaseVersion = getVersion(false);
        if (ZrLogUtil.greatThenCurrentVersion(lastReleaseVersion.getBuildId(), lastReleaseVersion.getBuildDate(), lastReleaseVersion.getVersion())) {
            LOGGER.info("ZrLog New release update found new [" + lastReleaseVersion.getVersion() + "-" + lastReleaseVersion.getBuildId() + "]");
        }
        return lastVersion.getBuildDate().after(lastReleaseVersion.getBuildDate()) ? lastVersion : lastReleaseVersion;
    }

    private static String getJsonFilename() {
        if (Constants.runMode == RunMode.JAR || Constants.runMode == RunMode.NATIVE_AGENT || Constants.runMode == RunMode.DEV) {
            return "last.version.json";
        }
        return "last." + Constants.getRealFileArch() + ".version.json";
    }

    public static void main(String[] args) throws IOException, URISyntaxException, ParseException, InterruptedException {
        Version version = getVersion(true);
    }

    private static Version getVersion(boolean preview) throws IOException, URISyntaxException, InterruptedException, ParseException {
        String versionUrl = BlogBuildInfoUtil.getResourceDownloadUrl() + "/" + (preview ? "preview" : "release") + "/" + getJsonFilename() + "?_" + System.currentTimeMillis() + "&v=" + BlogBuildInfoUtil.getBuildId();
        String txtContent = HttpUtil.getInstance().getSuccessTextByUrl(versionUrl);
        if (StringUtils.isEmpty(txtContent)) {
            Version errorVersion = new Version();
            errorVersion.setBuildDate(new Date(0));
            errorVersion.setBuildId("000000");
            return errorVersion;
        }
        Version versionInfo = new Gson().fromJson(txtContent.trim(), Version.class);
        Date versionDate = new SimpleDateFormat(Constants.DATE_FORMAT_PATTERN).parse(versionInfo.getReleaseDate());
        versionInfo.setBuildDate(versionDate);
        versionInfo.setReleaseDate(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(versionDate));
        //手动设置对应ChangeLog
        String language;
        if(Objects.nonNull(Constants.zrLogConfig)) {
            language = (String) Constants.zrLogConfig.getWebSite().get("language");
        } else {
            language = "zh_CN";
        }
        if (Objects.isNull(language)) {
            versionInfo.setChangeLog("");
        } else {
            Map<String, Object> langRes = I18nUtil.getI18nVOCache().getBackend().get(language);
            if (Objects.isNull(langRes)) {
                versionInfo.setChangeLog("");
            } else {
                versionInfo.setChangeLog(UpdateVersionPlugin.getChangeLog(versionInfo.getVersion(), versionInfo.getBuildDate(), versionInfo.getBuildId(), langRes));
            }
        }
        return versionInfo;
    }

    public Version getVersion() {
        return version;
    }
}
