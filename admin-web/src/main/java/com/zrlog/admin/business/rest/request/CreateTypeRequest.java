package com.zrlog.admin.business.rest.request;

import com.hibegin.common.util.StringUtils;
import com.zrlog.common.Validator;
import com.zrlog.common.exception.ArgsException;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.util.Objects;

public class CreateTypeRequest implements Validator {

    private String typeName;
    private String remark;
    private String alias;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public void doValid() {
        if (Objects.isNull(alias) || alias.trim().isEmpty()) {
            throw new ArgsException("alias");
        }
        if (Objects.isNull(typeName) || typeName.trim().isEmpty()) {
            throw new ArgsException("typeName");
        }
    }

    @Override
    public void doClean() {
        if (StringUtils.isNotEmpty(this.getAlias())) {
            this.setAlias(Jsoup.clean(this.getAlias(), Safelist.none()));
        }
        if (StringUtils.isNotEmpty(this.getTypeName())) {
            this.setTypeName(Jsoup.clean(this.getTypeName(), Safelist.none()));
        }
        if (StringUtils.isNotEmpty(this.getRemark())) {
            this.setRemark(Jsoup.clean(this.getRemark(), Safelist.basicWithImages()));
        }
    }
}
