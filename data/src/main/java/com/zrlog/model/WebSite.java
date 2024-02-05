package com.zrlog.model;


import com.hibegin.dao.DAO;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 存放全局的设置，比如网站标题，关键字，插件，主题的配置信息等，当字典表处理即可，对应数据库的website表
 */
public class WebSite extends DAO {

    public WebSite() {
        this.tableName = "website";
        this.pk = "siteId";
    }

    public Map<String, Object> getWebSite() {
        Map<String, Object> webSites = new HashMap<>();
        List<Map<String, Object>> lw;
        try {
            lw = queryList(ALL);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        for (Map<String, Object> webSite : lw) {
            webSites.put((String) webSite.get("name"), webSite.get("value"));
            webSites.put(webSite.get("name") + "Remark", webSite.get("remark"));
        }
        if(Objects.isNull(webSites.get("changyan_status"))){
            webSites.put("changyan_status","off");
        }
        return webSites;
    }

    public boolean updateByKV(String name, Object value) throws SQLException {
        if (queryFirstObj("select siteId from " + tableName + " where name=?", name) != null) {
            execute("update " + tableName + " set value=? where name=?", value, name);
        } else {
            execute("insert " + tableName + "(`value`,`name`) value(?,?)", value, name);
        }
        return true;
    }

    public String getStringValueByName(String name) {
        try {
            Object value = queryFirstObj("select value from " + tableName + " where name=?", name);
            if (Objects.isNull(value)) {
                return "";
            }
            return value.toString();
        } catch (Exception e) {
            //ignore，比如在未安装时，会有该异常但是不影响逻辑
        }
        return "";
    }

    public boolean getBoolValueByName(String name) throws SQLException {
        Map<String, Object> webSite = queryFirstWithParams("select value from website where name=?", name);
        //数据库varchar导致这里使用1进行比较
        return webSite != null && webSite.get("value") instanceof String && "1".equals(webSite.get("value"));
    }
}
