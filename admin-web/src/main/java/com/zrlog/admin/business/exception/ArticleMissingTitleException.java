package com.zrlog.admin.business.exception;

import com.zrlog.common.exception.AbstractBusinessException;

public class ArticleMissingTitleException extends AbstractBusinessException {
    @Override
    public int getError() {
        return 9026;
    }

    @Override
    public String getMessage() {
        return "文章标题不能为空";
    }
}
