package com.zrlog.admin.business.rest.request;

import com.hibegin.common.util.StringUtils;
import com.zrlog.admin.business.exception.ArgsException;
import com.zrlog.common.Validator;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.net.URI;
import java.util.Objects;

public class CreateNavRequest implements Validator {

    private String navName;
    private String url;
    private Long sort;

    public String getNavName() {
        return navName;
    }

    public void setNavName(String navName) {
        this.navName = navName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
        if (Objects.isNull(navName) || navName.trim().isEmpty()) {
            throw new ArgsException("navName");
        }
    }

    @Override
    public void doClean() {
        if (StringUtils.isNotEmpty(this.getNavName())) {
            this.setNavName(Jsoup.clean(this.getNavName(), Safelist.none()));
        }
        if (StringUtils.isNotEmpty(this.getUrl())) {
            this.setUrl(URI.create(this.getUrl()).toString());
        }
    }
}
