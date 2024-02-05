package com.zrlog.business.exception;

import com.zrlog.common.exception.AbstractBusinessException;

public class MissingDbPortException extends AbstractBusinessException {
    @Override
    public int getError() {
        return 9022;
    }

    @Override
    public String getMessage() {
        return "数据库端口不能为空";
    }
}
