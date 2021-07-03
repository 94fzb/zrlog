package com.zrlog.admin.business.rest.base;

public class CreateLinkRequest {

    private String linkName;
    private String url;
    private String alt;
    private Long sort;

    public String getLinkName() {
        return linkName;
    }

    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public Long getSort() {
        return sort;
    }

    public void setSort(Long sort) {
        this.sort = sort;
    }
}
