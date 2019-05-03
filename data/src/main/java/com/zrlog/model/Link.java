package com.zrlog.model;

import com.jfinal.plugin.activerecord.Model;
import com.zrlog.common.request.PageableRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 存放程序的友情链接，对应数据库link表
 */
public class Link extends Model<Link> {
    public static final String TABLE_NAME = "link";

    public List<Link> find() {
        return find("select linkName,linkId as id,sort,url,alt from " + TABLE_NAME + " order by sort");
    }

    public Map<String, Object> find(PageableRequest page) {
        Map<String, Object> data = new HashMap<>();
        data.put("rows", find("select linkName,linkId as id,sort,url,alt from " + TABLE_NAME + " order by sort limit ?,?", page.getOffset(), page.getRows()));
        ModelUtil.fillPageData(this, page.getPage(), page.getRows(), "from link", data, new Object[0]);
        return data;
    }
}
