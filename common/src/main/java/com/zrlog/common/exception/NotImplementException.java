package com.zrlog.common.exception;

public class NotImplementException extends AbstractBusinessException {
    @Override
    public int getError() {
        return 88;
    }
}
