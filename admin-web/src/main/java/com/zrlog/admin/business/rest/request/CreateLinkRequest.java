package com.zrlog.admin.business.rest.request;

import com.hibegin.common.util.StringUtils;
import com.zrlog.common.Validator;
import com.zrlog.common.exception.ArgsException;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.net.URI;
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

    @Override
    public void doClean() {
        if (StringUtils.isNotEmpty(this.getAlt())) {
            this.setAlt(Jsoup.clean(this.getAlt(), Safelist.basic()));
        }
        if (StringUtils.isNotEmpty(this.getLinkName())) {
            this.setLinkName(Jsoup.clean(this.getLinkName(), Safelist.basic()));
        }
        if (StringUtils.isNotEmpty(this.getUrl())) {
            this.setUrl(URI.create(this.getUrl()).toString());
        }
    }
}
