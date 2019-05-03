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
    public static final String TABLE_NAME = "type";

    public List<Type> find() {
        return find("select t.typeId as id,t.alias,t.typeName,t.remark,(select count(logId) from " + Log.TABLE_NAME +
                " where rubbish=? and privacy=? and typeid=t.typeid) as typeamount from " + TABLE_NAME + " t", false, false);
    }

    public Map<String, Object> find(PageableRequest page) {
        Map<String, Object> data = new HashMap<>();
        data.put("rows", find("select t.typeId as id,t.alias,t.typeName,t.remark,(select count(logId) from " + Log.TABLE_NAME +
                        " where typeid=t.typeid) as typeamount from " + TABLE_NAME + " t limit ?,?",
                page.getOffset(), page.getRows()));
        ModelUtil.fillPageData(this, page.getPage(), page.getRows(), "from " + TABLE_NAME, data, new Object[0]);
        return data;
    }

    public Type findByAlias(String alias) {
        return findFirst("select * from " + TABLE_NAME + " where alias=?", alias);
    }

}
