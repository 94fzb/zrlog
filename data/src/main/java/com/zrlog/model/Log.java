package com.zrlog.model;

import com.hibegin.common.util.StringUtils;
import com.hibegin.dao.DAO;
import com.zrlog.common.rest.request.PageRequest;
import com.zrlog.data.dto.PageData;
import com.zrlog.util.ParseUtil;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 存放文章数据，对应数据的log表。
 */
public class Log extends DAO implements Serializable {

    public static final String TABLE_NAME = "log";

    public Log() {
        this.tableName = TABLE_NAME;
        this.pk = "logId";
    }

    public Map<String, Object> findByIdOrAlias(Object idOrAlias) throws SQLException {
        if (idOrAlias == null) {
            return null;
        }
        if (idOrAlias instanceof Integer || ParseUtil.isNumeric((String) idOrAlias)) {
            String sql =
                    "select l.*,last_update_date as lastUpdateDate,u.userName,(select count(commentId) from " + Comment.TABLE_NAME + " where logId=l.logId) commentSize ,t.alias as typeAlias,t.typeName as typeName  from " + tableName + " l inner join user u,type t where t.typeId=l.typeId and u.userId=l.userId and rubbish=? and privacy=? and l.logId=?";
            Map<String, Object> log = queryFirstWithParams(sql, false, false, idOrAlias);
            if (log != null) {
                return log;
            }
        }
        String sql =
                "select l.*,last_update_date as lastUpdateDate,u.userName,(select count(commentId) from " + Comment.TABLE_NAME + " where logId=l.logId) commentSize ,t.alias as typeAlias,t.typeName as typeName  from " + tableName + " l inner join user u,type t where t.typeId=l.typeId and u.userId=l.userId and rubbish=? and privacy=? and l.alias=?";
        return queryFirstWithParams(sql, false, false, idOrAlias);
    }

    /**
     * 这个用于Admin 进行查询不检查
     */
    public Map<String, Object> adminFindByIdOrAlias(Object idOrAlias) throws SQLException {
        if (idOrAlias == null) {
            return null;
        }
        if (idOrAlias instanceof Integer || ParseUtil.isNumeric((String) idOrAlias)) {
            String sql =
                    "select l.*,last_update_date as lastUpdateDate,u.userName,(select count(commentId) from " + Comment.TABLE_NAME + " where logId=l.logId) commentSize ,t.alias as typeAlias,t.typeName as typeName  from " + tableName + " l inner join user u,type t where t.typeId=l.typeId and u.userId=l.userId and l.logId=?";
            Map<String, Object> log = queryFirstWithParams(sql, idOrAlias);
            if (log != null) {
                return log;
            }
        }
        String sql =
                "select l.*,last_update_date as lastUpdateDate,u.userName,(select count(commentId) from " + Comment.TABLE_NAME + " where logId=l.logId) commentSize ,t.alias as typeAlias,t.typeName as typeName  from " + tableName + " l inner join user u,type t where t.typeId=l.typeId and u.userId=l.userId and l.alias=?";
        return queryFirstWithParams(sql, idOrAlias);
    }

    public Map<String, Object> findLastLog(int id) throws SQLException {
        String lastLogSql =
                "select l.alias as alias,l.title as title from " + tableName + " l where rubbish=? and " + "privacy" + "=? and l.logId<? order by logId desc";
        return queryFirstWithParams(lastLogSql, false, false, id);

    }

    public Map<String, Object> findNextLog(int id) throws SQLException {
        String nextLogSql =
                "select l.alias as alias,l.title as title from " + tableName + " l where rubbish=? and " + "privacy" + "=? and l.logId>?";
        return queryFirstWithParams(nextLogSql, false, false, id);
    }

    public int findMaxId() throws SQLException {
        return (int) queryFirstObj("select max(logId) max from " + tableName);

    }

    public PageData<Map<String, Object>> visitorFind(PageRequest pageRequest, String keywords) throws SQLException {
        if (StringUtils.isEmpty(keywords)) {
            PageData<Map<String, Object>> data = new PageData<>();
            String sql =
                    "select l.*,t.typeName,t.alias as typeAlias,u.userName,(select count(commentId) from " + Comment.TABLE_NAME + " where logId=l.logId) commentSize from " + tableName + " l inner join user u inner join type t where rubbish=? and privacy=? and u.userId=l.userId and t.typeid=l.typeid  order by l.logId desc limit  ?,?";

            data.setRows(queryListWithParams(sql, false, false, pageRequest.getOffset(), pageRequest.getSize()));
            ModelUtil.fillPageData(this,
                    "from " + tableName + " l inner join user u where rubbish=? and privacy=? " + "and u.userId=l" + ".userId ", data, new Object[]{false, false});
            return data;
        }
        PageData<Map<String, Object>> data = new PageData<>();
        String sql =
                "select l.*,t.typeName,t.alias as typeAlias,(select count(commentId) from " + Comment.TABLE_NAME + " "
                        + "where logId=l.logId) commentSize,u.userName from " + tableName + " l inner join user u," + "type" + " t where rubbish=? and privacy=? and u.userId=l.userId and t.typeId=l.typeId and (l" + ".title " + "like ? or l.plain_content like ?) order by l.logId desc limit ?,?";
        data.setRows(queryListWithParams(sql, false, false, "%" + keywords + "%", "%" + keywords + "%", pageRequest.getOffset(),
                pageRequest.getSize()));
        ModelUtil.fillPageData(this, "from " + tableName + " l inner join user u,type t where rubbish=? and " +
                        "privacy=? and u.userId=l.userId and t.typeId=l.typeId and (l.title like ? or l.plain_content like ?)"
                , data, new Object[]{false, false, "%" + keywords + "%", "%" + keywords + "%"});
        return data;
    }

    /**
     * 管理员查询文章
     */
    public PageData<Map<String, Object>> adminFind(PageRequest pageRequest, String keywords) throws SQLException {
        PageData<Map<String, Object>> data = new PageData<>();
        String searchKeywords = "";
        List<Object> searchParam = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        if (StringUtils.isNotEmpty(keywords)) {
            searchKeywords = " and (l.title like ? or l.plain_content like ? or l.keywords like ? or l.alias like ?)";
            searchParam.add("%" + keywords + "%");
            searchParam.add("%" + keywords + "%");
            searchParam.add("%" + keywords + "%");
            searchParam.add("%" + keywords + "%");
            params.addAll(searchParam);
        }
        String pageSort = getPageSort(pageRequest);
        params.add(pageRequest.getOffset());
        params.add(pageRequest.getSize());
        String sql =
                "select l.*,l.privacy privacy,t.typeName,l.logId as id,l.last_update_date as lastUpdateDate,t" +
                        ".alias as typeAlias,u.userName,(select count(commentId) from " + Comment.TABLE_NAME + " " +
                        "where " + "logId=l.logId ) commentSize from " + tableName + " l inner join user u inner " + "join type t where u" + ".userId=l.userId" + searchKeywords + " and t.typeid=l.typeid order " + "by " + pageSort + " limit ?,?";
        data.setRows(findEntry(sql, params.toArray()));
        ModelUtil.fillPageData(this,
                "from " + tableName + " l inner join user u where u.userId=l.userId " + searchKeywords, data,
                searchParam.toArray());
        return data;
    }

    private static String getPageSort(PageRequest pageRequest) {
        String pageSort = "l.logId desc";
        String sortField = pageRequest.getSort();
        String order = pageRequest.getOrder();
        if (order != null && !order.isEmpty() && sortField != null && !sortField.isEmpty()) {
            sortField = switch (sortField) {
                case "typeName" -> "typeId";
                case "privacy" -> "privacy";
                case "lastUpdateDate" -> "last_update_date";
                default -> "logId";
            };
            pageSort = "l." + sortField + " " + order;
        }
        return pageSort;
    }

    public PageData<Map<String, Object>> findByTypeAlias(int page, int pageSize, String typeAlias) throws SQLException {
        PageData<Map<String, Object>> data = new PageData<>();

        String sql =
                "select l.*,t.typeName,t.alias  as typeAlias,(select count(commentId) from " + Comment.TABLE_NAME +
                        " where logId=l.logId ) commentSize,u.userName from " + tableName + " l inner join user u," + "type t where rubbish=? and privacy=? and u.userId=l.userId and t.typeId=l.typeId and t" + ".alias=? order by l.logId desc limit ?,?";
        data.setRows(queryListWithParams(sql, false, false, typeAlias, new PageRequest(page, pageSize).getOffset(), pageSize));

        ModelUtil.fillPageData(this, "from " + tableName + " l inner join user u,type t where u.userId=l.userId and "
                + "t.typeId=l.typeId and rubbish=? and privacy=? and t.alias=?", data, new Object[]{false, false,
                typeAlias});
        return data;
    }

    public Map<String, Long> getArchives() throws SQLException {
        List<Map<String, Object>> lo =
                queryListWithParams("select  releaseTime from " + tableName + "  where rubbish=? and privacy=? " + "order by " + "releaseTime desc", false, false);
        Map<String, Long> archives = new LinkedHashMap<>();
        for (Map<String, Object> entry : lo) {
            Object value = entry.get("releaseTime");
            if (value != null) {
                String key;
                if (value instanceof LocalDateTime) {
                    key = ((LocalDateTime) value).format(DateTimeFormatter.ofPattern("yyyy_MM"));
                } else if (value instanceof Timestamp) {
                    key = new SimpleDateFormat("yyyy_MM").format(new Date(((Timestamp) value).getTime()));
                } else {
                    key = "";
                }
                if (archives.containsKey(key)) {
                    archives.put(key, archives.get(key) + 1);
                } else {
                    archives.put(key, 1L);
                }
            }
        }
        return archives;
    }

    public PageData<Map<String, Object>> findByTag(int page, int pageSize, String tag) throws SQLException {
        PageData<Map<String, Object>> data = new PageData<>();
        String sql =
                "select l.*,t.typeName,t.alias  as typeAlias,(select count(commentId) from " + Comment.TABLE_NAME +
                        " where logId=l.logId) commentSize,u.userName from " + tableName + " l inner join user u," + "type t where rubbish=? and privacy=? and u.userId=l.userId and t.typeId=l.typeId and (l" + ".keywords like ? or l.keywords like ? or l.keywords like ? or l.keywords= ?) order by l" + ".logId desc limit ?,?";
        data.setRows(queryListWithParams(sql, false, false, tag + ",%", "%," + tag + ",%", "%," + tag, tag,
                new PageRequest(page, pageSize).getOffset(), pageSize));
        ModelUtil.fillPageData(this, "from " + tableName + " l inner join user u,type t where rubbish=? and " +
                "privacy=? and u.userId=l.userId and t.typeId=l.typeId and  (l.keywords like ? or l.keywords like ? " + "or l.keywords like ? or l.keywords= ?)", data, new Object[]{false, false, tag + ",%", "%," + tag + ",%", "%," + tag, tag});
        return data;
    }

    private List<Map<String, Object>> findEntry(String sql, Object[] paras) throws SQLException {
        return queryListWithParams(sql, paras);
    }

    public PageData<Map<String, Object>> findByDate(int page, int pageSize, String date) throws SQLException {
        PageData<Map<String, Object>> data = new PageData<>();
        String sql =
                "select l.*,t.typeName,t.alias as typeAlias,(select count(commentId) from " + Comment.TABLE_NAME + " "
                        + "where logId=l.logId ) commentSize,u.userName from " + tableName + " l inner join user u," + "type t where rubbish=? and privacy=? and u.userId=l.userId and t.typeId=l.typeId and " + "DATE_FORMAT(releaseTime,'%Y_%m')=? order by l.logId desc limit ?,?";
        data.setRows(queryListWithParams(sql, false, false, date, new PageRequest(page, pageSize).getOffset(), pageSize));
        ModelUtil.fillPageData(this, "from " + tableName + " l inner join user u,type t where rubbish=? and " +
                        "privacy=? and u.userId=l.userId and t.typeId=l.typeId and  DATE_FORMAT(releaseTime,'%Y_%m')=?", data
                , new Object[]{false, false, date});
        return data;
    }

    /**
     *
     */
    public void clickAdd(Object idOrAlias) throws SQLException {
        Map<String, Object> log = adminFindByIdOrAlias(idOrAlias);
        if (Objects.isNull(log)) {
            return;
        }
        new Log().set("click", ((Integer) log.get("click")) + 1).updateById(log.get("logId"));
    }

    public BigDecimal sumClick() throws SQLException {
        BigDecimal sum = (BigDecimal) queryFirstObj("select sum(click) from " + tableName);
        return sum == null ? new BigDecimal(0) : sum;
    }

    public long count() {
        try {
            return (long) queryFirstObj("select count(1) as count from " + tableName + " where rubbish=? and privacy=?", false,
                    false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public long countByTypeId(Integer typeId) {
        try {
            return (long) queryFirstObj("select count(1) as count from " + tableName + " where typeId=?", typeId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public long adminCount() {
        try {
            return (long) queryFirstObj("select count(1) as count from " + tableName);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
