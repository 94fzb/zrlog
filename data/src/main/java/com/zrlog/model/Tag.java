package com.zrlog.model;

import com.hibegin.common.util.LoggerUtil;
import com.hibegin.common.util.StringUtils;
import com.hibegin.dao.DAO;
import com.zrlog.common.rest.request.PageRequest;
import com.zrlog.data.dto.PageData;

import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 统计文章中标签出现的次数，方便展示。文章的数据发生变化后，会自动更新记录，无需额外程序控制，对应数据库的tag表
 */
public class Tag extends DAO {

    private static final Logger LOGGER = LoggerUtil.getLogger(Tag.class);

    public Tag() {
        this.tableName = "tag";
        this.pk = "tagId";
    }

    public List<Map<String, Object>> findAll() throws SQLException {
        return queryList("tagId as id,text,count");
    }

    private Set<String> strToSet(String str) {
        Set<String> tags = new HashSet<>();
        for (String tag : str.split(",")) {
            if (!tag.trim().isEmpty()) {
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

    public void refreshTag() throws SQLException {
        execute("delete from " + tableName);
        Map<String, Integer> countMap = new HashMap<>();
        List<Map<String, Object>> logs = new Log().queryListWithParams("select keywords from " + Log.TABLE_NAME + " where rubbish=? and privacy=? ", false, false);
        for (Map<String, Object> log : logs) {
            if (StringUtils.isNotEmpty((String) log.get("keywords")) && !((String) log.get("keywords")).trim().isEmpty()) {
                Set<String> tagSet = strToSet(log.get("keywords") + ",");
                for (String tag : tagSet) {
                    countMap.merge(tag, 1, Integer::sum);
                }
            }
        }
        int count = 1;
        for (Map.Entry<String, Integer> tag : countMap.entrySet()) {
            try {
                new Tag().set("tagId", count++).set("text", tag.getKey()).set("count", tag.getValue()).save();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "save error", e);
            }

        }
    }

    private void insertTag(Set<String> now) {
        for (String add : now) {
            try {
                Map<String, Object> t = queryFirstWithParams("select * from " + tableName + " where text=?", add);
                if (t == null) {
                    new Tag().set("text", add).set("count", 1).save();
                } else {
                    new Tag().set("count", (int) t.get("count") + 1).updateById(t.get(pk));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }
    }

    private void deleteTag(Set<String> old) {
        for (String del : old) {
            try {
                Map<String, Object> t = queryFirstWithParams("select * from " + tableName + " where text=?", del);
                if (t != null) {
                    if ((int) t.get("count") > 1) {
                        new Tag().set("count", (int) t.get("count") - 1).updateById(t.get(pk));
                    } else {
                        new Tag().deleteById((int) t.get(pk));
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }
        throw new RuntimeException();
    }

    public PageData<Map<String, Object>> find(PageRequest page) throws SQLException {
        PageData<Map<String, Object>> data = new PageData<>();
        data.setRows(queryListWithParams("select tagId as id,text,count from " + tableName + " limit ?,?", page.getOffset(), page.getSize()));
        ModelUtil.fillPageData(this, "from " + tableName, data, new Object[0]);
        return data;
    }
}
