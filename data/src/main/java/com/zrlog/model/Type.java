package com.zrlog.model;

import com.jfinal.plugin.activerecord.Model;
import com.zrlog.common.rest.request.PageRequest;
import com.zrlog.data.dto.PageData;

import java.util.List;

/**
 * 存放文章的分类信息，对应数据的type表
 */
public class Type extends Model<Type> {
    public static final String TABLE_NAME = "type";

    public List<Type> findAll() {
        return find("select t.typeId as id,t.alias,t.typeName,t.remark,(select count(logId) from " + Log.TABLE_NAME +
                " where rubbish=? and privacy=? and typeid=t.typeid) as typeamount from " + TABLE_NAME + " t", false, false);
    }

    public PageData<Type> find(PageRequest page) {
        PageData<Type> response = new PageData<>();
        response.setRows(find("select t.typeId as id,t.alias,t.typeName,t.remark,(select count(logId) from " + Log.TABLE_NAME +
                        " where typeid=t.typeid) as typeamount from " + TABLE_NAME + " t limit ?,?",
                page.getOffset(), page.getSize()));
        ModelUtil.fillPageData(this, "from " + TABLE_NAME, response, new Object[0]);
        return response;
    }

    public Type findByAlias(String alias) {
        return findFirst("select * from " + TABLE_NAME + " where alias=?", alias);
    }

}
