package com.fzb.blog.model;

import com.fzb.blog.util.ParseUtil;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 存放文章数据，对应数据的log表。
 */
public class Log extends Model<Log> implements Serializable {
    public static final Log dao = new Log();
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private boolean pre;
    private boolean rubbish;

    public Log(boolean pre, boolean rubbish) {
        this.rubbish = rubbish;
        this.pre = pre;
    }

    public Log() {
    }

    public Log getLogById(Object id) {
        if (id != null) {
            String sql = "select l.*,u.userName,(select count(commentId) from comment where logId=l.logId) commentSize ,t.alias as typeAlias,t.typeName as typeName  from log l inner join user u,type t where t.typeId=l.typeId and u.userId=l.userId and rubbish=? and private=? and l.logId=?";
            Log log = findFirst(sql, rubbish, pre, id);
            if (log == null) {
                sql = "select l.*,u.userName,(select count(commentId) from comment where logId=l.logId) commentSize ,t.alias as typeAlias,t.typeName as typeName  from log l inner join user u,type t where t.typeId=l.typeId and u.userId=l.userId and rubbish=? and private=? and l.alias=?";
                log = findFirst(sql, rubbish, pre, id);
            }
            if (log != null) {
                return log;
            }
        }
        return null;
    }

    /**
     * 这个用于Admin 进行查询不检查
     *
     * @param id
     * @return
     */
    public Log adminQueryLogByLogId(Object id) {
        if (id != null) {
            String sql;
            Log log;
            sql = "select l.*,u.userName,(select count(commentId) from comment where logId=l.logId) commentSize ,t.alias as typeAlias,t.typeName as typeName  from log l inner join user u,type t where t.typeId=l.typeId and u.userId=l.userId and l.logId=?";
            log = findFirst(sql, id);
            if (log == null) {
                sql = "select l.*,u.userName,(select count(commentId) from comment where logId=l.logId) commentSize ,t.alias as typeAlias,t.typeName as typeName  from log l inner join user u,type t where t.typeId=l.typeId and u.userId=l.userId and l.alias=?";
                log = findFirst(sql, id);
            }
            return log;
        }
        return null;
    }

    public Log getLastLog(int id, String notFoundDesc) {
        String lastLogSql = "select l.alias as alias,l.title as title from log l where rubbish=? and private=? and l.logId<? order by logId desc";
        Log log = findFirst(lastLogSql,
                rubbish, pre, id);
        if (log == null) {
            log = new Log().set("alias", id)
                    .set("title", notFoundDesc);
        }
        return log;
    }

    public Log getNextLog(int id, String notFoundDesc) {
        String nextLogSql = "select l.alias as alias,l.title as title from log l where rubbish=? and private=? and l.logId>?";
        Log log = findFirst(nextLogSql,
                rubbish, pre, id);
        if (log == null) {
            log = new Log().set("alias", id)
                    .set("title", notFoundDesc);
        }
        return log;
    }

    public int getMaxRecord() {
        Log log = findFirst("select max(logId) max from log ");
        if (log.getInt("max") != null) {
            return log.getInt("max");
        }
        return 0;
    }

    public Map<String, Object> getLogsByPage(int page, int pageSize) {
        Map<String, Object> data = new HashMap<String, Object>();
        String sql = "select l.*,t.typeName,t.alias as typeAlias,u.userName,(select count(commentId) from comment where logId=l.logId) commentSize from log l inner join user u inner join type t where rubbish=? and private=? and u.userId=l.userId and t.typeid=l.typeid  order by l.logId desc limit  ?,?";

        data.put(
                "rows",
                find(sql,
                        rubbish, pre, ParseUtil.getFirstRecord(page,
                                pageSize), pageSize));
        fillData(page, pageSize,
                "from log l inner join user u where rubbish=? and private=? and u.userId=l.userId ", data,
                new Object[]{rubbish, pre});
        return data;
    }

    public Map<String, Object> queryAll(int page, int pageSize, String keywords, String order, String field) {
        Map<String, Object> data = new HashMap<String, Object>();
        String searchKeywords = "";
        if (keywords != null && !"".equals(keywords)) {
            searchKeywords = " and (l.title like '%" + keywords + "%' or l.content like '%" + keywords + "%' or l.keywords like '%" + keywords + "%')";
        }
        String pageSort = "l.logId desc";
        if (order != null && !"".equals(order) && field != null && !"".equals(field)) {
            if ("id".equals(field)) {
                field = "logId";
            }
            pageSort = "l." + field + " " + order;
        }
        String sql = "select l.*,l.private _private,t.typeName,l.logId as id,t.alias as typeAlias,u.userName,(select count(commentId) from comment where logId=l.logId ) commentSize from log l inner join user u inner join type t where u.userId=l.userId" + searchKeywords + " and t.typeid=l.typeid order by " + pageSort + " limit ?,?";
        data.put(
                "rows",
                findEntry(sql,
                        ParseUtil.getFirstRecord(page,
                                pageSize), pageSize));
        fillData(page, pageSize,
                "from log l inner join user u where u.userId=l.userId " + searchKeywords, data,
                new Object[]{});
        return data;
    }

    public Map<String, Object> getLogsBySort(int page, int pageSize,
                                             String typeAlias) {
        Map<String, Object> data = new HashMap<String, Object>();
        String sql = "select l.*,t.typeName,t.alias  as typeAlias,(select count(commentId) from comment where logId=l.logId ) commentSize,u.userName from log l inner join user u,type t where rubbish=? and private=? and u.userId=l.userId and t.typeId=l.typeId and t.alias=? order by l.logId desc limit ?,?";
        data.put(
                "rows",
                find(sql,
                        rubbish, pre,
                        typeAlias,
                        ParseUtil.getFirstRecord(page,
                                pageSize), pageSize));

        fillData(
                page,
                pageSize,
                "from log l inner join user u,type t where u.userId=l.userId and t.typeId=l.typeId and rubbish=? and private=? and t.alias=?",
                data, new Object[]{rubbish, pre, typeAlias});
        return data;
    }

    private void fillData(int page, int pageSize, String where,
                          Map<String, Object> data, Object[] obj) {
        if (((List) data.get("rows")).size() > 0) {
            data.put("page", page);
            long count = findFirst("select count(l.logId) cnt " + where,
                    obj).getLong("cnt");
            data.put("total",
                    ParseUtil.getTotalPate(count, pageSize));
            data.put("records", count);
        } else {
            data.clear();
        }
    }

    public Map<String, Long> getArchives() {
        List<Timestamp> lo = Db
                .query("select  releaseTime from log  where rubbish=? and private=? order by logId desc", rubbish, pre);
        Map<String, Long> archives = new LinkedHashMap<String, Long>();
        for (Timestamp objects : lo) {
            String key = new SimpleDateFormat("yyyy_MM").format(new Date(objects.getTime()));
            if (archives.containsKey(key)) {
                archives.put(key, archives.get(key) + 1);
            } else {
                archives.put(key, 1L);
            }
        }
        return archives;
    }

    public Map<String, Object> getLogsByTag(int page, int pageSize, String tag) {
        Map<String, Object> data = new HashMap<String, Object>();
        String sql = "select l.*,t.typeName,t.alias  as typeAlias,(select count(commentId) from comment where logId=l.logId) commentSize,u.userName from log l inner join user u,type t where rubbish=? and private=? and u.userId=l.userId and t.typeId=l.typeId and (l.keywords like ? or l.keywords like ? or l.keywords like ? or l.keywords= ?) order by l.logId desc limit ?,?";
        data.put(
                "rows",
                find(sql,
                        rubbish, pre,
                        tag + ",%",
                        "%," + tag + ",%",
                        "%," + tag,
                        tag,
                        ParseUtil.getFirstRecord(page,
                                pageSize), pageSize));
        fillData(
                page,
                pageSize,
                "from log l inner join user u,type t where rubbish=? and private=? and u.userId=l.userId and t.typeId=l.typeId and  (l.keywords like ? or l.keywords like ? or l.keywords like ? or l.keywords= ?)",
                data, new Object[]{rubbish, pre, tag + ",%", "%," + tag + ",%", "%," + tag,
                        tag});
        return data;
    }

    private List<Map<String, Object>> findEntry(String sql, Object... paras) {
        List<Log> logList = find(sql, paras);
        List<Map<String, Object>> convertList = new ArrayList<Map<String, Object>>();
        for (Log log : logList) {
            convertList.add(log.getAttrs());
        }
        return convertList;
    }

    public Map<String, Object> getLogsByData(int page, int pageSize, String date) {
        Map<String, Object> data = new HashMap<String, Object>();
        String sql = "select l.*,t.typeName,t.alias as typeAlias,(select count(commentId) from comment where logId=l.logId ) commentSize,u.userName from log l inner join user u,type t where rubbish=? and private=? and u.userId=l.userId and t.typeId=l.typeId and DATE_FORMAT(releaseTime,'%Y_%m')=? order by l.logId desc limit ?,?";
        data.put(
                "rows",
                find(sql,
                        rubbish, pre,
                        date,
                        ParseUtil.getFirstRecord(page,
                                pageSize), pageSize));
        fillData(
                page,
                pageSize,
                "from log l inner join user u,type t where rubbish=? and private=? and u.userId=l.userId and t.typeId=l.typeId and  DATE_FORMAT(releaseTime,'%Y_%m')=?",
                data, new Object[]{rubbish, pre, date});
        return data;
    }

    public Map<String, Object> getLogsByTitleOrContent(int page, int pageSize,
                                                       String key) {
        Map<String, Object> data = new HashMap<String, Object>();
        String sql = "select l.*,t.typeName,t.alias as typeAlias,(select count(commentId) from comment where logId=l.logId) commentSize,u.userName from log l inner join user u,type t where rubbish=? and private=? and u.userId=l.userId and t.typeId=l.typeId and (l.title like ? or l.content like ?) order by l.logId desc limit ?,?";
        data.put(
                "rows",
                find(sql,
                        rubbish, pre,
                        "%" + key + "%",
                        "%" + key + "%",
                        ParseUtil.getFirstRecord(page,
                                pageSize), pageSize));
        fillData(
                page,
                pageSize,
                "from log l inner join user u,type t where rubbish=? and private=? and u.userId=l.userId and t.typeId=l.typeId and (l.title like ? or l.content like ?)",
                data, new Object[]{rubbish, pre, "%" + key + "%", "%" + key + "%"});
        return data;
    }

    public void clickChange(int logId) {
        Log log = findById(logId);
        if (log != null) {
            Integer click = log.get("click");
            log.set("logId", logId).set("click", click + 1).update();
        }
    }

    public BigDecimal getAllClick() {
        String sql = "select sum(click) from log";
        return findFirst(sql).getBigDecimal("sum(click)");
    }

    @Override
    public Map<String, Object> getAttrs() {
        return super.getAttrs();
    }

    public long getTotalArticleSize() {
        String sql = "select count(1) as count from log where rubbish=? and private=?";
        return findFirst(sql, false, false).getLong("count");
    }
}
