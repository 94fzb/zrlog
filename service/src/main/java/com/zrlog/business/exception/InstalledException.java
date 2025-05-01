package com.zrlog.business.exception;

import com.zrlog.common.exception.AbstractBusinessException;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.ZrLogUtil;

public class InstalledException extends AbstractBusinessException {
    @Override
    public int getError() {
        return 9020;
    }

    @Override
    public String getMessage() {
        return I18nUtil.getInstallStringFromRes(ZrLogUtil.isWarMode() ? "installedWarTips" : "installedTips");
    }
}
