package com.zrlog.admin.business.exception;

import com.zrlog.common.exception.AbstractBusinessException;
import com.zrlog.util.I18nUtil;

public class TemplateExistsException extends AbstractBusinessException {
    @Override
    public int getError() {
        return 9014;
    }

    @Override
    public String getMessage() {
        return I18nUtil.getBlogStringFromRes("templateExists");
    }
}
