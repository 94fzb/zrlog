package com.zrlog.model;


import com.hibegin.dao.DAO;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 存放了与侧边栏相关的数据，主要用于保存侧边栏的key，对应数据库的plugin表（目前实现方式不友好，建议直接使用主题设置，替换该设置）
 */
public class Plugin extends DAO {

    public Plugin() {
        this.tableName = "plugin";
    }

    public List<Map<String, Object>> findAll() throws SQLException {
        return queryListWithParams("select * from " + tableName + " where level>?", 0);
    }
}
