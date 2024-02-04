package com.zrlog.admin.business.exception;

import com.zrlog.common.exception.AbstractBusinessException;

public class ArticleMissingTypeException extends AbstractBusinessException {
    @Override
    public int getError() {
        return 9027;
    }

    @Override
    public String getMessage() {
        return "文章分类不能为空";
    }
}
