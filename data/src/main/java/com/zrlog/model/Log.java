package com.zrlog.model;

import com.hibegin.common.util.StringUtils;
import com.zrlog.common.rest.request.OrderBy;
import com.zrlog.common.rest.request.PageRequest;
import com.zrlog.common.rest.request.PageRequestImpl;
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
public class Log extends BasePageableDAO implements Serializable {

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

    public long findMaxId() throws SQLException {
        Number number = (Number) queryFirstObj("select max(logId) max from " + tableName);
        if (Objects.isNull(number)) {
            return 0;
        }
        return number.longValue();

    }

    public PageData<Map<String, Object>> visitorFind(PageRequest pageRequest, String keywords) {
        if (StringUtils.isEmpty(keywords)) {
            String sql =
                    "select l.*,t.typeName,t.alias as typeAlias,u.userName,(select count(commentId) from " + Comment.TABLE_NAME + " where logId=l.logId) commentSize from " + tableName + " l inner join user u inner join type t where rubbish=? and privacy=? and u.userId=l.userId and t.typeid=l.typeid  order by l.logId desc";
            return queryPageData(sql, pageRequest, new Object[]{false, false});
        }
        String sql =
                "select l.*,t.typeName,t.alias as typeAlias,(select count(commentId) from " + Comment.TABLE_NAME + " "
                        + "where logId=l.logId) commentSize,u.userName from " + tableName + " l inner join user u," + "type" + " t where rubbish=? and privacy=? and u.userId=l.userId and t.typeId=l.typeId and (l" + ".title " + "like ? or l.plain_content like ?) order by l.logId desc";
        return queryPageData(sql, pageRequest, new Object[]{false, false, "%" + keywords + "%", "%" + keywords + "%"});
    }

    /**
     * 管理员查询文章
     */
    public PageData<Map<String, Object>> adminFind(PageRequest pageRequest, String keywords, String typeAlias) {
        String searchKeywords = "";
        List<Object> searchParam = new ArrayList<>();
        if (StringUtils.isNotEmpty(keywords)) {
            searchKeywords = " and (l.title like ? or l.plain_content like ? or l.keywords like ? or l.alias like ?)";
            searchParam.add("%" + keywords + "%");
            searchParam.add("%" + keywords + "%");
            searchParam.add("%" + keywords + "%");
            searchParam.add("%" + keywords + "%");
        }
        if (StringUtils.isNotEmpty(typeAlias)) {
            searchKeywords += " and t.alias = ?";
            searchParam.add(typeAlias);
        }
        String pageSort = getPageSort(pageRequest);
        String sql =
                "select l.*,t.typeName,l.logId as id,l.last_update_date as lastUpdateDate,t" +
                        ".alias as typeAlias,u.userName,(select count(commentId) from " + Comment.TABLE_NAME + " " +
                        "where " + "logId=l.logId ) commentSize from " + tableName + " l inner join user u inner " + "join type t where u" + ".userId=l.userId" + searchKeywords +
                        " and t.typeid=l.typeid and l.typeid is not null order " + "by " + pageSort;
        return queryPageData(
                sql, pageRequest,
                searchParam.toArray());
    }

    private static final Map<String, String> sortKeyMap = new HashMap<>();

    static {
        sortKeyMap.put("typeName", "l.typeId");
        sortKeyMap.put("releaseTime", "l.releaseTime");
        sortKeyMap.put("commentSize", "commentSize");
        sortKeyMap.put("privacy", "l.privacy");
        sortKeyMap.put("click", "l.click");
        sortKeyMap.put("lastUpdateDate", "l.last_update_date");
    }


    private static String getPageSort(PageRequest pageRequest) {
        List<OrderBy> orders = pageRequest.getSorts();
        if (orders == null || orders.isEmpty()) {
            return "l.logId desc";
        }
        StringBuilder orderSort = new StringBuilder();
        for (OrderBy orderBy : orders) {
            orderSort.append(sortKeyMap.getOrDefault(orderBy.getSortKey(), "l.logId"));
            orderSort.append(" ").append(orderBy.getDirection().name().toLowerCase());
        }
        return orderSort.toString();
    }

    public PageData<Map<String, Object>> findByTypeAlias(long page, long pageSize, String typeAlias) {
        String sql =
                "select l.*,t.typeName,t.alias  as typeAlias,(select count(commentId) from " + Comment.TABLE_NAME +
                        " where logId=l.logId ) commentSize,u.userName from " + tableName + " l inner join user u," + "type t where rubbish=? and privacy=? and u.userId=l.userId and t.typeId=l.typeId and t" + ".alias=? order by l.logId desc";
        return queryPageData(sql, new PageRequestImpl(page, pageSize), new Object[]{false, false, typeAlias});
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

    public Map<String, Long> getAdminArticleData() throws SQLException {
        List<Map<String, Object>> lo = queryListWithParams("select releaseTime from " + tableName + " order by releaseTime desc");
        Map<String, Long> archives = new LinkedHashMap<>();
        for (Map<String, Object> entry : lo) {
            Object value = entry.get("releaseTime");
            if (value != null) {
                String key;
                if (value instanceof LocalDateTime) {
                    key = ((LocalDateTime) value).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                } else if (value instanceof Timestamp) {
                    key = new SimpleDateFormat("yyyy-MM-dd").format(new Date(((Timestamp) value).getTime()));
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


    public PageData<Map<String, Object>> findByTag(long page, long pageSize, String tag) {
        String sql =
                "select l.*,t.typeName,t.alias  as typeAlias,(select count(commentId) from " + Comment.TABLE_NAME +
                        " where logId=l.logId) commentSize,u.userName from " + tableName + " l inner join user u," + "type t where rubbish=? and privacy=? and u.userId=l.userId and t.typeId=l.typeId and (l" + ".keywords like ? or l.keywords like ? or l.keywords like ? or l.keywords= ?) order by l" + ".logId desc";
        return queryPageData(sql, new PageRequestImpl(page, pageSize), new Object[]{false, false, tag + ",%", "%," + tag + ",%", "%," + tag, tag});
    }

    private List<Map<String, Object>> findEntry(String sql, Object[] paras) throws SQLException {
        return queryListWithParams(sql, paras);
    }

    public PageData<Map<String, Object>> findByDate(long page, long pageSize, String date) {
        String sql =
                "select l.*,t.typeName,t.alias as typeAlias,(select count(commentId) from " + Comment.TABLE_NAME + " "
                        + "where logId=l.logId ) commentSize,u.userName from " + tableName + " l inner join user u," + "type t where rubbish=? and privacy=? and u.userId=l.userId and t.typeId=l.typeId and " + "DATE_FORMAT(releaseTime,'%Y_%m')=? order by l.logId desc";
        return queryPageData(sql, new PageRequestImpl(page, pageSize)
                , new Object[]{false, false, date});
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

    public long getVisitorCount() {
        return visitorFind(new PageRequestImpl(1L, 0L), null).getTotalElements();
    }

    public long countByTypeId(Integer typeId) {
        try {
            return (long) queryFirstObj("select count(1) as count from " + tableName + " where typeId=?", typeId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public long getAdminCount() {
        return adminFind(new PageRequestImpl(1L, 0L), null, null).getTotalElements();
    }
}
