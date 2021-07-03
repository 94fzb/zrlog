package com.zrlog.admin.business.exception;

import com.zrlog.common.exception.AbstractBusinessException;
import com.zrlog.util.I18nUtil;

public class TemplatePathNotNullException extends AbstractBusinessException {
    @Override
    public int getError() {
        return 9015;
    }

    @Override
    public String getMessage() {
        return I18nUtil.getBlogStringFromRes("templatePathNotNull");
    }
}
