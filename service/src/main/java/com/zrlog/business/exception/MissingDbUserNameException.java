package com.zrlog.business.exception;

import com.zrlog.common.exception.AbstractBusinessException;

public class MissingDbUserNameException extends AbstractBusinessException {
    @Override
    public int getError() {
        return 9023;
    }

    @Override
    public String getMessage() {
        return "数据库用户名不能为空";
    }
}
