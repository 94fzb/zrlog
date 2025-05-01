package com.zrlog.admin.business.exception;

import com.zrlog.common.exception.AbstractBusinessException;
import com.zrlog.util.I18nUtil;

public class AdminAuthException extends AbstractBusinessException {
    @Override
    public int getError() {
        return 9001;
    }

    @Override
    public String getMessage() {
        return I18nUtil.getBackendStringFromRes("admin.session.timeout");
    }
}
