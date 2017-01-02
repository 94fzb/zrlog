package com.fzb.blog.model;

import com.fzb.blog.util.ParseUtil;
import com.jfinal.plugin.activerecord.Model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 存放导航条的信息，对应数据库的lognav表
 */
public class LogNav extends Model<LogNav> {
    public static final LogNav dao = new LogNav();

    public List<LogNav> queryAll() {
        String sql = "select l.navId as id,l.navName,l.url,l.sort from  lognav l order by sort";
        return find(sql);
    }

    public Map<String, Object> queryAll(Integer page, Integer pageSize) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put(
                "rows",
                find("select l.navId as id,l.navName,l.url,l.sort from  lognav l order by sort limit ?,?",
                        ParseUtil.getFirstRecord(page,
                                pageSize), pageSize));
        fillData(page, pageSize, "from lognav", data, new Object[0]);
        return data;
    }

    private void fillData(int page, int pageSize, String where,
                          Map<String, Object> data, Object[] obj) {
        if (((List) data.get("rows")).size() > 0) {
            data.put("page", page);
            long count = findFirst("select count(1) cnt " + where,
                    obj).getLong("cnt");
            data.put("total",
                    ParseUtil.getTotalPate(count, pageSize));
            data.put("records", count);
        } else {
            data.clear();
        }
    }
}
