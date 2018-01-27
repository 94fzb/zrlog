package com.zrlog.model;

import com.jfinal.plugin.activerecord.Model;
import com.zrlog.util.ParseUtil;

import java.util.List;
import java.util.Map;

class ModelUtil {

    static void fillPageData(Model<? extends Model> model, int page, int pageSize, String where, Map<String, Object> data, Object[] obj) {
        if (!((List) data.get("rows")).isEmpty()) {
            data.put("page", page);
            long count = model.findFirst("select count(1) cnt " + where, obj).getLong("cnt");
            data.put("total", ParseUtil.getTotalPate(count, pageSize));
            data.put("records", count);
        } else {
            data.clear();
        }
    }
}
