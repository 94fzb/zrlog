package com.zrlog.admin.business.exception;

import com.zrlog.common.exception.AbstractBusinessException;
import com.zrlog.util.I18nUtil;

public class UserNameOrPasswordException extends AbstractBusinessException {
    @Override
    public int getError() {
        return 9010;
    }

    @Override
    public String getMessage() {
        return I18nUtil.getBlogStringFromRes("userNameOrPasswordError");
    }
}
