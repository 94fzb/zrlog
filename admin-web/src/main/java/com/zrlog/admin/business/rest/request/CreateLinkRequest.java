package com.zrlog.admin.business.rest.request;

import com.zrlog.admin.business.exception.ArgsException;
import com.zrlog.common.Validator;

import java.util.Objects;

public class CreateLinkRequest implements Validator {

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

    @Override
    public void doValid() {
        if (Objects.isNull(url) || url.trim().isEmpty()) {
            throw new ArgsException("url");
        }
        if (Objects.isNull(linkName) || linkName.trim().isEmpty()) {
            throw new ArgsException("linkName");
        }
    }
}
