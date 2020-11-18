package com.zrlog.web.exception;

import com.zrlog.common.exception.AbstractBusinessException;

public class BadTemplatePathException extends AbstractBusinessException {

    private final String templatePath;

    public BadTemplatePathException(String templatePaht) {
        this.templatePath = templatePaht;
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
