package com.zrlog.admin.business.rest.request;

import com.zrlog.admin.business.exception.ArgsException;
import com.zrlog.common.Validator;

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
}
