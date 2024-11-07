package com.zrlog.admin.business.exception;

import com.zrlog.common.exception.AbstractBusinessException;
import com.zrlog.util.I18nUtil;

public class ArticleMissingTitleException extends AbstractBusinessException {
    @Override
    public int getError() {
        return 9026;
    }

    @Override
    public String getMessage() {
        return I18nUtil.getAdminStringFromRes("article_require_title");
    }
}
