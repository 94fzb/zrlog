package com.zrlog.model;

import com.hibegin.common.util.StringUtils;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.zrlog.common.request.PageableRequest;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 统计文章中标签出现的次数，方便展示。文章的数据发生变化后，会自动更新记录，无需额外程序控制，对应数据库的tag表
 */
public class Tag extends Model<Tag> {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Tag.class);

    public static final String TABLE_NAME = "tag";

    public List<Tag> find() {
        return find("select tagId as id,text,count from " + TABLE_NAME);
    }

    private Set<String> strToSet(String str) {
        Set<String> tags = new HashSet<>();
        for (String tag : str.split(",")) {
            if (tag.trim().length() > 0) {
                tags.add(tag.trim());
            }
        }
        return tags;
    }

    public boolean update(String nowTagStr, String oldTagStr) {
        String copyNewTagStr = nowTagStr;
        String copyOldTagStr = oldTagStr;
        if (copyNewTagStr == null && oldTagStr == null) {
            return true;
        }
        if (copyNewTagStr == null) {
            copyNewTagStr = "";
        }
        if (copyOldTagStr == null) {
            copyOldTagStr = "";
        }
        if (copyNewTagStr.equals(copyOldTagStr)) {
            return true;
        }
        String[] oldArr = copyOldTagStr.split(",");
        String[] nowArr = copyNewTagStr.split(",");
        Set<String> addSet = new HashSet<>();
        Set<String> deleteSet = new HashSet<>();
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

    public void refreshTag() {
        Db.update("delete from " + TABLE_NAME + "");
        Map<String, Integer> countMap = new HashMap<>();
        List<Log> logs = new Log().find("select keywords from " + Log.TABLE_NAME + " where rubbish=? and privacy=? ", false, false);
        for (Log log : logs) {
            if (StringUtils.isNotEmpty(log.getStr("keywords")) && log.getStr("keywords").trim().length() > 0) {
                Set<String> tagSet = strToSet(log.getStr("keywords") + ",");
                for (String tag : tagSet) {
                    countMap.merge(tag, 1, (a, b) -> a + b);
                }
            }
        }
        int count = 1;
        for (Map.Entry<String, Integer> tag : countMap.entrySet()) {
            try {
                new Tag().set("tagId", count++).set("text", tag.getKey()).set("count", tag.getValue()).save();
            } catch (Exception e) {
                LOGGER.warn("save error", e);
            }

        }
    }

    private void insertTag(Set<String> now) {
        for (String add : now) {
            Tag t = findFirst("select * from " + TABLE_NAME + " where text=?", add);
            if (t == null) {
                new Tag().set("text", add).set("count", 1).save();
            } else {
                t.set("count", t.getInt("count") + 1).update();
            }
        }
    }

    private void deleteTag(Set<String> old) {
        for (String del : old) {
            Tag t = findFirst("select * from " + TABLE_NAME + " where text=?", del);
            if (t != null) {
                if (t.getInt("count") > 1) {
                    t.set("count", t.getInt("count") - 1).update();
                } else {
                    t.delete();
                }
            }
        }
    }

    public Map<String, Object> find(PageableRequest page) {
        Map<String, Object> data = new HashMap<>();
        data.put("rows", find("select tagId as id,text,count from " + TABLE_NAME + " limit ?,?", page.getOffset(), page.getRows()));
        ModelUtil.fillPageData(this, page.getPage(), page.getRows(), "from " + TABLE_NAME + "", data, new Object[0]);
        return data;
    }
}
