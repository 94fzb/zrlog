package com.fzb.blog.model;

import com.fzb.common.util.ParseTools;
import com.jfinal.plugin.activerecord.Model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogNav extends Model<LogNav> {
    public static final LogNav dao = new LogNav();

    public List<LogNav> queryAll(String baseUrl) {
        String sql = "select l.navId as id,l.navName,l.url,l.sort from  lognav l order by sort";
        List<LogNav> navs = find(sql);
        for (LogNav logNav : navs) {
            if (logNav.get("url").toString().startsWith("/")) {
                logNav.set("url", baseUrl + logNav.getStr("url"));
            }
        }
        return navs;
    }

    public List<LogNav> queryAll() {
        String sql = "select l.navId as id,l.navName,l.url,l.sort from  lognav l order by sort";
        return find(sql);
    }

    public Map<String, Object> queryAll(Integer page, Integer pageSize) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put(
                "rows",
                find("select l.navId as id,l.navName,l.url,l.sort from  lognav l order by sort limit ?,?",
                        ParseTools.getFirstRecord(page,
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
                    ParseTools.getTotalPate(count, pageSize));
            data.put("records", count);
        } else {
            data.clear();
        }
    }
}
