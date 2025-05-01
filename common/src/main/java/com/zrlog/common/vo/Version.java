package com.zrlog.common.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * 这个对应了 last.runType.json 里面的字段
 */
public class Version implements Serializable {

    private String buildId;
    private String releaseDate;
    private String version;
    private Date buildDate;
    private long warFileSize;

    public long getWarFileSize() {
        return warFileSize;
    }

    public void setWarFileSize(long warFileSize) {
        this.warFileSize = warFileSize;
    }

    public long getZipFileSize() {
        return zipFileSize;
    }

    public void setZipFileSize(long zipFileSize) {
        this.zipFileSize = zipFileSize;
    }

    private long zipFileSize;
    private String changeLog;
    private String type;
    private String warDownloadUrl;

    public String getZipDownloadUrl() {
        return zipDownloadUrl;
    }

    public void setZipDownloadUrl(String zipDownloadUrl) {
        this.zipDownloadUrl = zipDownloadUrl;
    }

    private String zipDownloadUrl;
    private String warMd5sum;

    public String getZipMd5sum() {
        return zipMd5sum;
    }

    public void setZipMd5sum(String zipMd5sum) {
        this.zipMd5sum = zipMd5sum;
    }

    private String zipMd5sum;

    public String getBuildId() {
        return buildId;
    }

    public void setBuildId(String buildId) {
        this.buildId = buildId;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getChangeLog() {
        return changeLog;
    }

    public void setChangeLog(String changeLog) {
        this.changeLog = changeLog;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWarDownloadUrl() {
        return warDownloadUrl;
    }

    public void setWarDownloadUrl(String warDownloadUrl) {
        this.warDownloadUrl = warDownloadUrl;
    }

    public String getWarMd5sum() {
        return warMd5sum;
    }

    public void setWarMd5sum(String warMd5sum) {
        this.warMd5sum = warMd5sum;
    }

    public Date getBuildDate() {
        return buildDate;
    }

    public void setBuildDate(Date buildDate) {
        this.buildDate = buildDate;
    }
}
