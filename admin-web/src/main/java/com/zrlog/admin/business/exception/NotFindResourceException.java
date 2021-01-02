package com.zrlog.admin.business.exception;

import com.zrlog.common.exception.AbstractBusinessException;

public class NotFindResourceException extends AbstractBusinessException {
    @Override
    public int getError() {
        return 9014;
    }

    @Override
    public String getMessage() {
        return "没有对应的资源";
    }
}
