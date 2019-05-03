package com.zrlog.model;

import com.jfinal.plugin.activerecord.Model;
import com.zrlog.common.request.PageableRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 存放导航条的信息，对应数据库的lognav表
 */
public class LogNav extends Model<LogNav> {
    public static final String TABLE_NAME = "lognav";

    public List<LogNav> find() {
        return find("select l.navId as id,l.navName,l.url,l.sort from " + TABLE_NAME + " l order by sort");
    }

    public Map<String, Object> find(PageableRequest page) {
        Map<String, Object> data = new HashMap<>();
        data.put("rows", find("select l.navId as id,l.navName,l.url,l.sort from " + TABLE_NAME + " l order by sort limit ?,?", page.getOffset(), page.getRows()));
        ModelUtil.fillPageData(this, page.getPage(), page.getRows(), "from " + TABLE_NAME, data, new Object[0]);
        return data;
    }
}
