package com.zrlog.admin.business.exception;

import com.zrlog.common.exception.AbstractBusinessException;
import com.zrlog.util.I18nUtil;

import java.util.Objects;

public class ArgsException extends AbstractBusinessException {

    private final String args;

    public ArgsException() {
        args = "";
    }

    public ArgsException(String args) {
        this.args = args;
    }


    @Override
    public int getError() {
        return 9012;
    }

    @Override
    public String getMessage() {
        String namingArgs = "[" + Objects.requireNonNullElse(args, "") + "]";
        return namingArgs + ":" + I18nUtil.getBackendStringFromRes("argsError");
    }
}
