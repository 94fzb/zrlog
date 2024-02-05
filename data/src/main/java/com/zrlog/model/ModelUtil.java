package com.zrlog.model;

import com.hibegin.dao.DAO;
import com.zrlog.data.dto.PageData;

import java.sql.SQLException;
import java.util.Map;

class ModelUtil {

    static <T extends DAO> void fillPageData(T model, String where, PageData<Map<String, Object>> pageData, Object[] obj) throws SQLException {
        long count = (long) model.queryFirstObj("select count(1) cnt " + where, obj);
        pageData.setTotalElements(count);
    }
}
