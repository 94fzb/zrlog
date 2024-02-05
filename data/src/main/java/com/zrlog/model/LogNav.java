package com.zrlog.model;

import com.hibegin.dao.DAO;
import com.zrlog.common.rest.request.PageRequest;
import com.zrlog.data.dto.PageData;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 存放导航条的信息，对应数据库的lognav表
 */
public class LogNav extends DAO {

    public LogNav() {
        this.pk = "navId";
        this.tableName = "lognav";
    }

    public List<Map<String, Object>> findAll() throws SQLException {
        return queryListWithParams("select l.navId as id,l.navName,l.url,l.sort from " + tableName + " l order by sort");
    }

    public PageData<Map<String, Object>> find(PageRequest page) throws SQLException {
        PageData<Map<String, Object>> data = new PageData<>();
        data.setRows(queryListWithParams("select l.navId as id,l.navName,l.url,l.sort from " + tableName + " l order by sort limit ?,?", page.getOffset(), page.getSize()));
        ModelUtil.fillPageData(this, "from " + tableName, data, new Object[0]);
        return data;
    }
}
