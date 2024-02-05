package com.zrlog.model;

import com.hibegin.dao.DAO;
import com.zrlog.common.rest.request.PageRequest;
import com.zrlog.data.dto.PageData;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 存放程序的友情链接，对应数据库link表
 */
public class Link extends DAO {


    public Link() {
        this.tableName = "link";
        this.pk = "linkId";
    }

    public List<Map<String, Object>> findAll() throws SQLException {
        return queryListWithParams("select linkName,linkId as id,sort,url,alt from " + tableName + " order by sort");
    }

    public PageData<Map<String, Object>> find(PageRequest page) throws SQLException {
        PageData<Map<String, Object>> data = new PageData<>();
        data.setRows(queryListWithParams("select linkName,linkId as id,sort,url,alt from " + tableName + " order by sort limit ?,?", page.getOffset(), page.getSize()));
        ModelUtil.fillPageData(this, "from link", data, new Object[0]);
        return data;
    }
}
