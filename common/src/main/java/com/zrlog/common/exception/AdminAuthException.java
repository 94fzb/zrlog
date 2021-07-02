package com.zrlog.common.exception;

import com.zrlog.util.I18nUtil;

public class AdminAuthException extends AbstractBusinessException {
    @Override
    public int getError() {
        return 9001;
    }

    @Override
    public String getMessage() {
        return I18nUtil.getBlogStringFromRes("admin.session.timeout");
    }
}
