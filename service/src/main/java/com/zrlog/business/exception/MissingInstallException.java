package com.zrlog.business.exception;

import com.zrlog.common.exception.AbstractBusinessException;
import com.zrlog.util.I18nUtil;

public class MissingInstallException extends AbstractBusinessException {
    @Override
    public int getError() {
        return 9029;
    }

    @Override
    public String getMessage() {
        return I18nUtil.getBackendStringFromRes("missingInstall");
    }
}
