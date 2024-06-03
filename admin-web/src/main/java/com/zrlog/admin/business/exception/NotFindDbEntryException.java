package com.zrlog.admin.business.exception;

import com.zrlog.common.exception.AbstractBusinessException;
import com.zrlog.util.I18nUtil;

public class NotFindDbEntryException extends AbstractBusinessException {

    private final String msg;

    public NotFindDbEntryException() {
        this.msg = I18nUtil.getBlogStringFromRes("notFound");
    }

    @Override
    public int getError() {
        return 9014;
    }

    @Override
    public String getMessage() {
        return msg;
    }
}
