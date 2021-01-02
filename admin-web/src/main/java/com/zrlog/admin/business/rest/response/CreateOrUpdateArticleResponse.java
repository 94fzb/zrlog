package com.zrlog.admin.business.rest.response;

public class CreateOrUpdateArticleResponse {

    private Long logId;

    private String alias;

    private String thumbnail;

    private String digest;

    private Integer version;

    private Boolean rubbish;

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Boolean getRubbish() {
        return rubbish;
    }

    public void setRubbish(Boolean rubbish) {
        this.rubbish = rubbish;
    }
}
