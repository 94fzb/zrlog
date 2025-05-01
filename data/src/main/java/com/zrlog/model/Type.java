package com.zrlog.model;

import com.hibegin.common.dao.BasePageableDAO;
import com.hibegin.common.dao.dto.PageData;
import com.hibegin.common.dao.dto.PageRequest;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 存放文章的分类信息，对应数据的type表
 */
public class Type extends BasePageableDAO {

    public Type() {
        this.pk = "typeId";
        this.tableName = "type";
    }

    public List<Map<String, Object>> findAll() throws SQLException {
        return queryListWithParams("select t.typeId as id,t.alias,t.typeName,t.remark,t.arrange_plugin,(select count(logId) from " + Log.TABLE_NAME +
                " where rubbish=? and privacy=? and typeid=t.typeid) as typeamount from " + tableName + " t", false, false);
    }

    public PageData<Map<String, Object>> find(PageRequest page) throws SQLException {
        return queryPageData("select t.typeId as id,t.alias,t.typeName,t.remark,t.arrange_plugin,(select count(logId) from " + Log.TABLE_NAME +
                " where typeid=t.typeid) as typeamount from " + tableName + " t", page, new Object[0]);
    }

    public Map<String, Object> findByAlias(String alias) throws SQLException {
        return queryFirstWithParams("select * from " + tableName + " where alias=?", alias);
    }

}
