package com.zrlog.common.vo;

import java.io.Serializable;

/**
 * 这个对应了 last.runType.json 里面的字段
 */
public class Version implements Serializable {

    private String buildId;
    private String releaseDate;
    private String version;
    private long fileSize;

    public long getZipFileSize() {
        return zipFileSize;
    }

    public void setZipFileSize(long zipFileSize) {
        this.zipFileSize = zipFileSize;
    }

    private long zipFileSize;
    private String changeLog;
    private String type;
    private String downloadUrl;

    public String getZipDownloadUrl() {
        return zipDownloadUrl;
    }

    public void setZipDownloadUrl(String zipDownloadUrl) {
        this.zipDownloadUrl = zipDownloadUrl;
    }

    private String zipDownloadUrl;
    private String md5sum;

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

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
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

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getMd5sum() {
        return md5sum;
    }

    public void setMd5sum(String md5sum) {
        this.md5sum = md5sum;
    }


}
