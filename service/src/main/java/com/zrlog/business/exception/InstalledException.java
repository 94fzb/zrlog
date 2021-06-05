package com.zrlog.business.exception;

import com.zrlog.common.exception.AbstractBusinessException;
import com.zrlog.util.I18nUtil;

public class InstalledException extends AbstractBusinessException {
    @Override
    public int getError() {
        return 9020;
    }

    @Override
    public String getMessage() {
        return I18nUtil.getInstallStringFromRes("installed");
    }
}
