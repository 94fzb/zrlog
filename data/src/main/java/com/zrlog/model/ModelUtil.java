package com.zrlog.model;

import com.jfinal.plugin.activerecord.Model;
import com.zrlog.data.dto.PageData;

class ModelUtil {

    static <T extends Model<T>> void fillPageData(T model, String where, PageData<T> pageData, Object[] obj) {
        if (!pageData.getRows().isEmpty()) {
            long count = model.findFirst("select count(1) cnt " + where, obj).getLong("cnt");
            pageData.setTotalElements(count);
        }
    }
}
