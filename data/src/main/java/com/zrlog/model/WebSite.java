package com.zrlog.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 存放全局的设置，比如网站标题，关键字，插件，主题的配置信息等，当字典表处理即可，对应数据库的website表
 */
public class WebSite extends Model<WebSite> {
    public static final WebSite dao = new WebSite();
    public static final String TABLE_NAME = "website";

    public Map<String, Object> getWebSite() {
        Map<String, Object> webSites = new HashMap<>();
        List<WebSite> lw = find("select * from " + TABLE_NAME);
        for (WebSite webSite : lw) {
            webSites.put(webSite.getStr("name"), webSite.get("value"));
            webSites.put(webSite.getStr("name") + "Remark", webSite.get("remark"));
        }
        return webSites;
    }

    public boolean updateByKV(String name, Object value) {
        if (Db.queryInt("select siteId from " + TABLE_NAME + " where name=?", name) != null) {
            Db.update("update " + TABLE_NAME + " set value=? where name=?", value, name);
        } else {
            Db.update("insert " + TABLE_NAME + "(`value`,`name`) value(?,?)", value, name);
        }
        return true;
    }

    public String getStringValueByName(String name) {
        try {
            WebSite webSite = findFirst("select value from " + TABLE_NAME + " where name=?", name);
            if (webSite != null) {
                return webSite.get("value");
            }
        } catch (NullPointerException e) {
            //ignore，比如在未安装时，会有该异常但是不影响逻辑
        }
        return "";
    }

    public boolean getBoolValueByName(String name) {
        WebSite webSite = findFirst("select value from website where name=?", name);
        //数据库varchar导致这里使用1进行比较
        return webSite != null && webSite.get("value") instanceof String && "1".equals(webSite.get("value"));
    }
}
