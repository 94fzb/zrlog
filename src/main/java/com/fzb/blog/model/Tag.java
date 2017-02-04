package com.fzb.blog.model;

import com.fzb.blog.util.ParseUtil;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;

import java.util.*;
import java.util.Map.Entry;

/**
 * 统计文章中标签出现的次数，方便展示。文章的数据发生变化后，会自动更新记录，无需额外程序控制，对应数据库的tag表
 */
public class Tag extends Model<Tag> {
    public static final Tag dao = new Tag();

    public static void main(String[] args) {
        new Tag().update("java,java ", "C#,C# ");
    }

    public List<Tag> queryAll() {
        return find("select tagid as id,text,count from tag");
    }

    private Set<String> strToSet(String str) {
        Set<String> tags = new HashSet<String>();
        for (String tag : str.split(",")) {
            if (tag.trim().length() > 0) {
                tags.add(tag.trim());
            }
        }
        return tags;
    }

    public boolean update(String nowTagStr, String oldTagStr) {
        if (nowTagStr == null && oldTagStr == null) {
            return true;
        }
        if (nowTagStr == null) {
            nowTagStr = "";
        }
        if (oldTagStr == null) {
            oldTagStr = "";
        }
        if (nowTagStr.equals(oldTagStr)) {
            return true;
        }
        String[] oldArr = oldTagStr.split(",");
        String[] nowArr = nowTagStr.split(",");
        Set<String> addSet = new HashSet<String>();
        Set<String> deleteSet = new HashSet<String>();
        for (String str : nowArr) {
            addSet.add(str.trim());
        }
        for (String str : oldArr) {
            if (!addSet.contains(str)) {
                deleteSet.add(str.trim());
            } else {
                addSet.remove(str);
            }
        }
        insertTag(addSet);
        deleteTag(deleteSet);

        return true;
    }

    public boolean insertTag(String now) {
        return insertTag(strToSet(now));
    }

    private boolean insertTag(Set<String> now) {
        for (String add : now) {
            Tag t = dao.findFirst("select * from tag where text=?", add);
            if (t == null) {
                new Tag().set("text", add).set("count", 1).save();
            } else {
                t.set("count", t.getInt("count") + 1).update();
            }
        }
        return true;
    }

    public boolean deleteTag(String old) {
        return deleteTag(strToSet(old));
    }

    private boolean deleteTag(Set<String> old) {
        for (String del : old) {
            Tag t = dao.findFirst("select * from tag where text=?", del);
            if (t != null) {
                if (t.getInt("count") > 1) {
                    t.set("count", t.getInt("count") - 1).update();
                } else {
                    t.delete();
                }
            }
        }
        return true;
    }

    public void refreshTag() {
        Db.update("delete from tag");
        Map<String, Integer> countMap = new HashMap<String, Integer>();
        List<Log> logs = Log.dao.find("select * from log where rubbish=? and private=? ", false, false);
        for (Log log : logs) {
            Set<String> tagSet = strToSet(log.getStr("keywords") + ",");
            for (String tag : tagSet) {
                if (countMap.get(tag) != null) {
                    countMap.put(tag, countMap.get(tag) + 1);
                } else {
                    countMap.put(tag, 1);
                }
            }
        }
        int count = 1;
        for (Entry<String, Integer> tag : countMap.entrySet()) {
            new Tag().set("tagId", count++).set("text", tag.getKey()).set("count", tag.getValue()).save();
        }
    }

    public Map<String, Object> queryAll(Integer page, Integer pageSize) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("rows", find("select tagid as id,text,count from tag limit ?,?", ParseUtil.getFirstRecord(page, pageSize), pageSize));
        fillData(page, pageSize, "from tag", data, new Object[0]);
        return data;
    }

    private void fillData(int page, int pageSize, String where, Map<String, Object> data, Object[] obj) {
        if (((List) data.get("rows")).size() > 0) {
            data.put("page", page);
            long count = findFirst("select count(1) cnt " + where, obj).getLong("cnt");
            data.put("total", ParseUtil.getTotalPate(count, pageSize));
            data.put("records", count);
        } else {
            data.clear();
        }
    }

    public long countByText() {
        return 0;
    }
}
