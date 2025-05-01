package com.zrlog.common.exception;

public class MissingRequestBodyException extends AbstractBusinessException {
    @Override
    public int getError() {
        return 9030;
    }
}
