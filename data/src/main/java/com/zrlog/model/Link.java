package com.zrlog.model;

import com.jfinal.plugin.activerecord.Model;
import com.zrlog.common.rest.request.PageRequest;
import com.zrlog.data.dto.PageData;

import java.util.List;

/**
 * 存放程序的友情链接，对应数据库link表
 */
public class Link extends Model<Link> {
    public static final String TABLE_NAME = "link";

    public List<Link> findAll() {
        return find("select linkName,linkId as id,sort,url,alt from " + TABLE_NAME + " order by sort");
    }

    public PageData<Link> find(PageRequest page) {
        PageData<Link> data = new PageData<>();
        data.setRows(find("select linkName,linkId as id,sort,url,alt from " + TABLE_NAME + " order by sort limit ?,?", page.getOffset(), page.getSize()));
        ModelUtil.fillPageData(this, "from link", data, new Object[0]);
        return data;
    }
}
