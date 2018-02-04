package com.zrlog.model;

import com.jfinal.plugin.activerecord.Model;
import com.zrlog.common.request.PageableRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 存放文章的分类信息，对应数据的type表
 */
public class Type extends Model<Type> {
    public static final Type dao = new Type();
    public static final String TABLE_NAME = "type";

    public List<Type> find() {
        return dao.find("select t.typeId as id,t.alias,t.typeName,t.remark,(select count(logId) from log where rubbish=? and private=? and typeid=t.typeid) as typeamount from type t", false, false);
    }

    public Map<String, Object> find(PageableRequest page) {
        Map<String, Object> data = new HashMap<>();
        data.put("rows", find("select t.typeId as id,t.alias,t.typeName,t.remark,(select count(logId) from log where typeid=t.typeid) as typeamount from type t limit ?,?",
                page.getOffset(), page.getRows()));
        ModelUtil.fillPageData(this, page.getPage(), page.getRows(), "from type", data, new Object[0]);
        return data;
    }

    public Type findByAlias(String alias) {
        return dao.findFirst("select * from type where alias=?", alias);
    }

}
