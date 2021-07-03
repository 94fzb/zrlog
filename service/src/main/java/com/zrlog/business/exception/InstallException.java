package com.zrlog.business.exception;

import com.zrlog.business.type.TestConnectDbResult;
import com.zrlog.common.exception.AbstractBusinessException;
import com.zrlog.util.I18nUtil;

public class InstallException extends AbstractBusinessException {

    private final TestConnectDbResult result;

    public InstallException(TestConnectDbResult result) {
        this.result = result;
    }

    @Override
    public int getError() {
        return 9000;
    }

    @Override
    public String getMessage() {
        return "[Error-" + result + "] - " + I18nUtil.getInstallStringFromRes("connectDbError_" + result.getError());
    }
}
