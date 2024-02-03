package com.zrlog.model;

import com.hibegin.dao.DAO;
import com.zrlog.common.rest.request.PageRequest;
import com.zrlog.data.dto.PageData;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 存放文章的分类信息，对应数据的type表
 */
public class Type extends DAO {

    public Type() {
        this.pk = "typeId";
        this.tableName = "type";
    }

    public List<Map<String, Object>> findAll() throws SQLException {
        return queryListWithParams("select t.typeId as id,t.alias,t.typeName,t.remark,(select count(logId) from " + Log.TABLE_NAME +
                " where rubbish=? and privacy=? and typeid=t.typeid) as typeamount from " + tableName + " t", false, false);
    }

    public PageData<Map<String, Object>> find(PageRequest page) throws SQLException {
        PageData<Map<String, Object>> response = new PageData<>();
        response.setRows(queryListWithParams("select t.typeId as id,t.alias,t.typeName,t.remark,(select count(logId) from " + Log.TABLE_NAME +
                        " where typeid=t.typeid) as typeamount from " + tableName + " t limit ?,?",
                page.getOffset(), page.getSize()));
        ModelUtil.fillPageData(this, "from " + tableName, response, new Object[0]);
        return response;
    }

    public Map<String, Object> findByAlias(String alias) throws SQLException {
        return queryFirstWithParams("select * from " + tableName + " where alias=?", alias);
    }

}
