package com.zrlog.admin.business.exception;

import com.zrlog.common.exception.AbstractBusinessException;
import com.zrlog.util.I18nUtil;

public class DeleteTypeException extends AbstractBusinessException {
    @Override
    public int getError() {
        return 9025;
    }

    @Override
    public String getMessage() {
        return "该分类下存在文章，无法删除";
    }
}
