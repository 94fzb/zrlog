package com.zrlog.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.zrlog.util.ParseUtil;

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
    public static final String TABLE_NAME = "log";
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private boolean _private;
    private boolean rubbish;

    public Log(boolean _private, boolean rubbish) {
        this.rubbish = rubbish;
        this._private = _private;
    }

    public Log() {
    }

    public Log findById(Object id) {
        if (id != null) {
            String sql = "select l.*,last_update_date as lastUpdateDate,u.userName,(select count(commentId) from " + Comment.TABLE_NAME + " where logId=l.logId) commentSize ,t.alias as typeAlias,t.typeName as typeName  from " + TABLE_NAME + " l inner join user u,type t where t.typeId=l.typeId and u.userId=l.userId and rubbish=? and private=? and l.logId=?";
            Log log = findFirst(sql, rubbish, _private, id);
            if (log == null) {
                sql = "select l.*,last_update_date as lastUpdateDate,u.userName,(select count(commentId) from " + Comment.TABLE_NAME + " where logId=l.logId) commentSize ,t.alias as typeAlias,t.typeName as typeName  from " + TABLE_NAME + " l inner join user u,type t where t.typeId=l.typeId and u.userId=l.userId and rubbish=? and private=? and l.alias=?";
                log = findFirst(sql, rubbish, _private, id);
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
    public Log adminFindLogByLogId(Object id) {
        if (id != null) {
            String sql = "select l.*,last_update_date as lastUpdateDate,u.userName,(select count(commentId) from " + Comment.TABLE_NAME + " where logId=l.logId) commentSize ,t.alias as typeAlias,t.typeName as typeName  from " + TABLE_NAME + " l inner join user u,type t where t.typeId=l.typeId and u.userId=l.userId and l.logId=?";
            Log log = findFirst(sql, id);
            if (log == null) {
                sql = "select l.*,last_update_date as lastUpdateDate,u.userName,(select count(commentId) from " + Comment.TABLE_NAME + " where logId=l.logId) commentSize ,t.alias as typeAlias,t.typeName as typeName  from " + TABLE_NAME + " l inner join user u,type t where t.typeId=l.typeId and u.userId=l.userId and l.alias=?";
                log = findFirst(sql, id);
            }
            return log;
        }
        return null;
    }

    public Log findLastLog(int id, String notFoundDesc) {
        String lastLogSql = "select l.alias as alias,l.title as title from " + TABLE_NAME + " l where rubbish=? and private=? and l.logId<? order by logId desc";
        Log log = findFirst(lastLogSql, rubbish, _private, id);
        if (log == null) {
            log = new Log().set("alias", id).set("title", notFoundDesc);
        }
        return log;
    }

    public Log findNextLog(int id, String notFoundDesc) {
        String nextLogSql = "select l.alias as alias,l.title as title from " + TABLE_NAME + " l where rubbish=? and private=? and l.logId>?";
        Log log = findFirst(nextLogSql, rubbish, _private, id);
        if (log == null) {
            log = new Log().set("alias", id).set("title", notFoundDesc);
        }
        return log;
    }

    public int findMaxId() {
        Log log = findFirst("select max(logId) max from " + TABLE_NAME);
        if (log.getInt("max") != null) {
            return log.getInt("max");
        }
        return 0;
    }

    public Map<String, Object> find(int page, int pageSize) {
        Map<String, Object> data = new HashMap<>();
        String sql = "select l.*,t.typeName,t.alias as typeAlias,u.userName,(select count(commentId) from " + Comment.TABLE_NAME + " where logId=l.logId) commentSize from " + TABLE_NAME + " l inner join user u inner join type t where rubbish=? and private=? and u.userId=l.userId and t.typeid=l.typeid  order by l.logId desc limit  ?,?";

        data.put("rows", find(sql, rubbish, _private, ParseUtil.getFirstRecord(page, pageSize), pageSize));
        ModelUtil.fillPageData(this, page, pageSize, "from " + TABLE_NAME + " l inner join user u where rubbish=? and private=? and u.userId=l.userId ", data,
                new Object[]{rubbish, _private});
        return data;
    }

    /**
     * 管理员查询文章
     *
     * @param page
     * @param pageSize
     * @param keywords
     * @param order
     * @param field
     * @return
     */
    public Map<String, Object> find(int page, int pageSize, String keywords, String order, String field) {
        Map<String, Object> data = new HashMap<>();
        String searchKeywords = "";
        if (keywords != null && !"".equals(keywords)) {
            searchKeywords = " and (l.title like '%" + keywords + "%' or l.plain_content like '%" + keywords + "%' or l.keywords like '%" + keywords + "%')";
        }
        String pageSort = "l.logId desc";
        String copyField = field;
        if (order != null && !"".equals(order) && field != null && !"".equals(field)) {
            if ("id".equals(copyField)) {
                copyField = "logId";
            } else if ("typeName".equals(copyField)) {
                copyField = "typeId";
            } else if ("_private".equals(copyField)) {
                copyField = "private";
            } else if ("lastUpdateDate".equals(copyField)) {
                copyField = "last_update_date";
            }
            pageSort = "l." + copyField + " " + order;
        }
        String sql = "select l.*,l.private _private,t.typeName,l.logId as id,l.last_update_date as lastUpdateDate,t.alias as typeAlias,u.userName,(select count(commentId) from " + Comment.TABLE_NAME + " where logId=l.logId ) commentSize from " + TABLE_NAME + " l inner join user u inner join type t where u.userId=l.userId" + searchKeywords + " and t.typeid=l.typeid order by " + pageSort + " limit ?,?";
        data.put("rows", findEntry(sql, ParseUtil.getFirstRecord(page, pageSize), pageSize));
        ModelUtil.fillPageData(this, page, pageSize, "from " + TABLE_NAME + " l inner join user u where u.userId=l.userId " + searchKeywords, data, new Object[]{});
        return data;
    }

    public Map<String, Object> findByTypeAlias(int page, int pageSize, String typeAlias) {
        Map<String, Object> data = new HashMap<>();
        String sql = "select l.*,t.typeName,t.alias  as typeAlias,(select count(commentId) from " + Comment.TABLE_NAME + " where logId=l.logId ) commentSize,u.userName from " + TABLE_NAME + " l inner join user u,type t where rubbish=? and private=? and u.userId=l.userId and t.typeId=l.typeId and t.alias=? order by l.logId desc limit ?,?";
        data.put("rows", find(sql, rubbish, _private, typeAlias, ParseUtil.getFirstRecord(page, pageSize), pageSize));

        ModelUtil.fillPageData(this, page, pageSize,
                "from " + TABLE_NAME + " l inner join user u,type t where u.userId=l.userId and t.typeId=l.typeId and rubbish=? and private=? and t.alias=?",
                data, new Object[]{rubbish, _private, typeAlias});
        return data;
    }

    public Map<String, Long> getArchives() {
        List<Timestamp> lo = Db.query("select  releaseTime from " + TABLE_NAME + "  where rubbish=? and private=? order by logId desc", rubbish, _private);
        Map<String, Long> archives = new LinkedHashMap<>();
        for (Timestamp objects : lo) {
            if (objects != null) {
                String key = new SimpleDateFormat("yyyy_MM").format(new Date(objects.getTime()));
                if (archives.containsKey(key)) {
                    archives.put(key, archives.get(key) + 1);
                } else {
                    archives.put(key, 1L);
                }
            }
        }
        return archives;
    }

    public Map<String, Object> findByTag(int page, int pageSize, String tag) {
        Map<String, Object> data = new HashMap<>();
        String sql = "select l.*,t.typeName,t.alias  as typeAlias,(select count(commentId) from " + Comment.TABLE_NAME + " where logId=l.logId) commentSize,u.userName from " + TABLE_NAME + " l inner join user u,type t where rubbish=? and private=? and u.userId=l.userId and t.typeId=l.typeId and (l.keywords like ? or l.keywords like ? or l.keywords like ? or l.keywords= ?) order by l.logId desc limit ?,?";
        data.put("rows", find(sql, rubbish, _private, tag + ",%", "%," + tag + ",%", "%," + tag, tag, ParseUtil.getFirstRecord(page, pageSize), pageSize));
        ModelUtil.fillPageData(this, page, pageSize,
                "from " + TABLE_NAME + " l inner join user u,type t where rubbish=? and private=? and u.userId=l.userId and t.typeId=l.typeId and  (l.keywords like ? or l.keywords like ? or l.keywords like ? or l.keywords= ?)",
                data, new Object[]{rubbish, _private, tag + ",%", "%," + tag + ",%", "%," + tag, tag});
        return data;
    }

    private List<Map<String, Object>> findEntry(String sql, Object... paras) {
        List<Log> logList = find(sql, paras);
        List<Map<String, Object>> convertList = new ArrayList<>();
        for (Log log : logList) {
            convertList.add(log.getAttrs());
        }
        return convertList;
    }

    public Map<String, Object> findByDate(int page, int pageSize, String date) {
        Map<String, Object> data = new HashMap<>();
        String sql = "select l.*,t.typeName,t.alias as typeAlias,(select count(commentId) from " + Comment.TABLE_NAME + " where logId=l.logId ) commentSize,u.userName from " + TABLE_NAME + " l inner join user u,type t where rubbish=? and private=? and u.userId=l.userId and t.typeId=l.typeId and DATE_FORMAT(releaseTime,'%Y_%m')=? order by l.logId desc limit ?,?";
        data.put("rows", find(sql, rubbish, _private, date, ParseUtil.getFirstRecord(page, pageSize), pageSize));
        ModelUtil.fillPageData(this, page, pageSize,
                "from " + TABLE_NAME + " l inner join user u,type t where rubbish=? and private=? and u.userId=l.userId and t.typeId=l.typeId and  DATE_FORMAT(releaseTime,'%Y_%m')=?",
                data, new Object[]{rubbish, _private, date});
        return data;
    }

    public Map<String, Object> findByTitleOrPlainContentLike(int page, int pageSize, String key) {
        Map<String, Object> data = new HashMap<>();
        String sql = "select l.*,t.typeName,t.alias as typeAlias,(select count(commentId) from " + Comment.TABLE_NAME + " where logId=l.logId) commentSize,u.userName from " + TABLE_NAME + " l inner join user u,type t where rubbish=? and private=? and u.userId=l.userId and t.typeId=l.typeId and (l.title like ? or l.plain_content like ?) order by l.logId desc limit ?,?";
        data.put("rows", find(sql, rubbish, _private, "%" + key + "%", "%" + key + "%", ParseUtil.getFirstRecord(page, pageSize), pageSize));
        ModelUtil.fillPageData(this, page, pageSize,
                "from " + TABLE_NAME + " l inner join user u,type t where rubbish=? and private=? and u.userId=l.userId and t.typeId=l.typeId and (l.title like ? or l.plain_content like ?)",
                data, new Object[]{rubbish, _private, "%" + key + "%", "%" + key + "%"});
        return data;
    }

    /**
     * @param logId
     */
    public void incrClick(int logId) {
        Log log = findById(logId);
        if (log != null) {
            log.set("logId", logId).set("click", log.getInt("click") + 1).update();
        }
    }

    public BigDecimal sumAllClick() {
        return findFirst("select sum(click) from " + TABLE_NAME).getBigDecimal("sum(click)");
    }

    public Map<String, Object> getAttrs() {
        return super._getAttrs();
    }

    public long count() {
        return findFirst("select count(1) as count from " + TABLE_NAME + " where rubbish=? and private=?", false, false).getLong("count");
    }
}
