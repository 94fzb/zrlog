package com.fzb.blog.model;

import com.fzb.blog.util.ParseUtil;
import com.jfinal.plugin.activerecord.Model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 存放文章的分类信息，对应数据的type表
 */
public class Type extends Model<Type> {
    public static final Type dao = new Type();

    public List<Type> queryAll() {
        return dao
                .find("select t.typeId as id,t.alias,t.typeName,t.remark,(select count(logId) from log where rubbish=? and private=? and typeid=t.typeid) as typeamount from type t",
                        false, false);
    }

    public Map<String, Object> queryAll(Integer page, Integer pageSize) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put(
                "rows",
                find("select t.typeId as id,t.alias,t.typeName,t.remark,(select count(logId) from log where typeid=t.typeid) as typeamount from type t limit ?,?",
                        ParseUtil.getFirstRecord(page,
                                pageSize), pageSize));
        fillData(page, pageSize, "from type", data, new Object[0]);
        return data;
    }

    private void fillData(int page, int pageSize, String where,
                          Map<String, Object> data, Object[] obj) {
        if (((List) data.get("rows")).size() > 0) {
            data.put("page", page);
            long count = findFirst("select count(1) cnt " + where, obj).getLong("cnt");
            data.put("total", ParseUtil.getTotalPate(count, pageSize));
            data.put("records", count);
        } else {
            data.clear();
        }
    }

    public Type findByAlias(String alias) {
        return dao.findFirst("select * from type where alias=?", alias);
    }

    public long countByName() {
        return 0;
    }
}
