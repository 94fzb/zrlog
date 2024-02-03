package com.zrlog.business.exception;

import com.zrlog.common.exception.AbstractBusinessException;

public class MissingDbHostException extends AbstractBusinessException {
    @Override
    public int getError() {
        return 9021;
    }

    @Override
    public String getMessage() {
        return "数据库地址不能为空";
    }
}
