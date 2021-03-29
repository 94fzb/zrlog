package com.zrlog.admin.business.exception;

import com.zrlog.common.exception.AbstractBusinessException;

public class UpdateArticleExpireException extends AbstractBusinessException {
    @Override
    public int getError() {
        return 9094;
    }

    @Override
    public String getMessage() {
        return "文章内容已经过期，请刷新网页，在最新的内容上面进行修改";
    }
}
