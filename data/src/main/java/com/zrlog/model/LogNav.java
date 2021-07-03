package com.zrlog.model;

import com.jfinal.plugin.activerecord.Model;
import com.zrlog.common.rest.request.PageRequest;
import com.zrlog.data.dto.PageData;

import java.util.List;

/**
 * 存放导航条的信息，对应数据库的lognav表
 */
public class LogNav extends Model<LogNav> {
    public static final String TABLE_NAME = "lognav";

    public List<LogNav> findAll() {
        return find("select l.navId as id,l.navName,l.url,l.sort from " + TABLE_NAME + " l order by sort");
    }

    public PageData<LogNav> find(PageRequest page) {
        PageData<LogNav> data = new PageData<>();
        data.setRows(find("select l.navId as id,l.navName,l.url,l.sort from " + TABLE_NAME + " l order by sort limit ?,?", page.getOffset(), page.getSize()));
        ModelUtil.fillPageData(this, "from " + TABLE_NAME, data, new Object[0]);
        return data;
    }
}
