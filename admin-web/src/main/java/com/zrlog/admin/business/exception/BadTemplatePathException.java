package com.zrlog.admin.business.exception;

import com.zrlog.common.exception.AbstractBusinessException;

public class BadTemplatePathException extends AbstractBusinessException {

    private final String templatePath;

    public BadTemplatePathException(String templatePath) {
        this.templatePath = templatePath;
    }

    @Override
    public int getError() {
        return 9002;
    }

    @Override
    public String getMessage() {
        return "主题地址[" + templatePath + "]错误";
    }
}
