package com.zrlog.common.exception;

public abstract class AbstractBusinessException extends RuntimeException {

    public abstract int getError();
}
