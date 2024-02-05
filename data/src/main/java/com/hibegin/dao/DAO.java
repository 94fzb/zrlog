package com.hibegin.dao;

import com.hibegin.common.util.LoggerUtil;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DAO implements IDAO {

    public static final String[] ALL = "*".split(" ");
    public static Logger log = LoggerUtil.getLogger(DAO.class);
    private static DataSource dataSource;
    protected String tableName;
    protected String pk = "id";
    protected QueryRunner queryRunner;
    private Map<String, Object> attrs = new HashMap<>();

    private final boolean dev;

    public DAO() {
        queryRunner = new QueryRunner(dataSource, true);
        this.dev = false;
    }

    public static void main(String[] args) throws SQLException {
        DAO dao = new DAO();
        //log.info(dao.queryFirstObj("select count(1) from SYS_USER where name!=?", "null"));
        //log.info(dao.queryList("id,name"));
    }

    public static void setDs(DataSource ds) {
        dataSource = ds;
    }

    public Map<String, Object> getAttrs() {
        return attrs;
    }

    public DAO setAttrs(Map<String, Object> attrs) {
        this.attrs = attrs;
        return this;
    }

    /**
     * 在插入数据时取的Map中key
     *
     * @return
     */
    protected String getAttrsKey() {
        StringBuilder sb = new StringBuilder();
        for (String label : attrs.keySet()) {
            sb.append(label);
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    /**
     * 插入数据时使用
     *
     * @return
     */
    protected String appendParams() {
        StringBuilder sb = new StringBuilder();
        int length = attrs.size();
        for (int i = 0; i < length; i++) {
            sb.append("?,");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    /**
     * 用于插入(删除，查询)数据中使用
     *
     * @param map
     * @return
     */
    protected Object[] getMapValues(Map<String, Object> map) {

        Object[] obj = new Object[map.size()];
        int i = 0;
        for (Object value : map.values()) {
            obj[i++] = value;
        }
        return obj;
    }

    /**
     * 用于更新中使用
     *
     * @param attrs 更新后的数据
     * @param cond  更新的条件
     * @return
     */
    protected Object[] getAttsValues(Map<String, Object> attrs, Map<String, Object> cond) {
        Object[] obj = new Object[attrs.size() + cond.size()];
        int i = 0;
        for (Object value : attrs.values()) {
            obj[i++] = value;
        }
        for (Object value : cond.values()) {
            obj[i++] = value;
        }
        return obj;
    }

    /**
     * 查询更新时使用
     *
     * @param map
     * @return
     */
    protected String condsMap2Str(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("1=1");
        for (Entry<String, Object> m : map.entrySet()) {
            sb.append(" and ").append(m.getKey()).append("=?");
        }
        return sb.toString();
    }

    /**
     * 更新
     *
     * @param map
     * @return
     */
    protected String attrsMap2Str(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        for (Entry<String, Object> m : map.entrySet()) {
            sb.append(" ").append(m.getKey()).append("=?,");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public DAO set(String attr, Object value) {
        attrs.put(attr, value);
        return this;
    }

    /* (non-Javadoc)
     * @see com.mir.server.dao.IDAO#save()
     */
    @Override
    public boolean save() throws SQLException {
        if (attrs.isEmpty()) {
            //抛出异常信息
            return false;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("insert into ");
        sb.append(tableName);
        sb.append("(");
        sb.append(getAttrsKey());
        sb.append(") values(");
        sb.append(appendParams());
        sb.append(")");
        if (dev) {
            log.log(Level.FINER, sb.toString());
        }
        return queryRunner.update(sb.toString(), getMapValues(attrs)) > 0;
    }

    /* (non-Javadoc)
     * @see com.mir.server.dao.IDAO#update(java.com.fzb.common.util.Map)
     */
    @Override
    public boolean update(Map<String, Object> conditions) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("update ");
        sb.append(tableName);
        sb.append(" set ");
        sb.append(attrsMap2Str(attrs));
        sb.append(" where ");
        sb.append(condsMap2Str(conditions));
        if (dev) {
            log.log(Level.FINER, sb.toString());
        }
        return queryRunner.update(sb.toString(), getAttsValues(attrs, conditions)) > 0;
    }

    @Override
    public boolean updateById(Object id) throws SQLException {
        return update(Map.of(pk, id));
    }

    /* (non-Javadoc)
     * @see com.mir.server.dao.IDAO#delete()
     */
    @Override
    public boolean delete() throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("delete from ");
        sb.append(tableName);
        sb.append(" where ");
        sb.append(condsMap2Str(attrs));
        if (dev) {
            log.log(Level.FINER, sb.toString());
        }
        return queryRunner.update(sb.toString(), getMapValues(attrs)) > 0;
    }

    /* (non-Javadoc)
     * @see com.mir.server.dao.IDAO#deleteById(int)
     */
    @Override
    public boolean deleteById(int id) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("delete from ");
        sb.append(tableName);
        sb.append(" where ").append(pk).append("=?");
        if (dev) {
            log.log(Level.FINER, sb.toString());
        }
        return queryRunner.update(sb.toString(), id) > 0;
    }

    /* (non-Javadoc)
     * @see com.mir.server.dao.IDAO#queryFrist(java.lang.String)
     */
    @Override
    public Map<String, Object> queryFirst(String... columns) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("select ");
        for (String str : columns) {
            sb.append(str).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(" from ");
        sb.append(tableName);
        Map<String, Object> map = null;
        if (!attrs.isEmpty()) {
            sb.append(" where ");
            sb.append(condsMap2Str(attrs));
            map = queryRunner.query(sb.toString(), new MapHandler(), getMapValues(attrs));
        } else {
            map = queryRunner.query(sb.toString(), new MapHandler());
        }
        if (dev) {
            log.log(Level.FINER, sb.toString());
        }
        return map;
    }

    public Object queryFirst(String column) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("select ");
        sb.append(column).append(",");
        sb.deleteCharAt(sb.length() - 1);
        sb.append(" from ");
        sb.append(tableName);
        Map<String, Object> map = null;
        if (!attrs.isEmpty()) {
            sb.append(" where ");
            sb.append(condsMap2Str(attrs));
            map = queryRunner.query(sb + " order by " + column, new MapHandler(), getMapValues(attrs));
        } else {
            map = queryRunner.query(sb + " order by " + column, new MapHandler());
        }
        if (dev) {
            log.log(Level.FINER, sb.toString());
        }
        if (map != null) {
            return map.get(column);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see com.mir.server.dao.IDAO#queryList()
     */
    @Override
    public List<Map<String, Object>> queryList(String... columns) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("select ");
        for (String str : columns) {
            sb.append(str + ",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(" from ");
        sb.append(tableName);
        if (!attrs.isEmpty()) {
            sb.append(" where ");
            sb.append(condsMap2Str(attrs));
            if (dev) {
                log.log(Level.INFO, sb.toString());
            }
            return queryRunner.query(sb.toString(), new MapListHandler(), getMapValues(attrs));
        } else {
            if (dev) {
                log.log(Level.INFO, sb.toString());
            }
            return queryRunner.query(sb.toString(), new MapListHandler());
        }
    }

    @Override
    public List<Map<String, Object>> queryPagination(int page, int rows, String... columns) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("select ");
        for (String str : columns) {
            sb.append(str).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(" from ");
        sb.append(tableName);
        sb.append(" limit ");
        if (page < 1) {
            page = 1;
        }
        sb.append(((page - 1) * rows)).append(",");
        sb.append(rows);
        List<Map<String, Object>> lmap = null;
        if (!attrs.isEmpty()) {
            sb.append(" where ");
            sb.append(condsMap2Str(attrs));
            lmap = queryRunner.query(sb.toString(), new MapListHandler(), getMapValues(attrs));
        } else {
            lmap = queryRunner.query(sb.toString(), new MapListHandler());
        }
        if (dev) {
            log.log(Level.FINER, sb.toString());
        }
        return lmap;
    }

    @Override
    public List<Map<String, Object>> queryPagination(String sql, int page,
                                                     int rows) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append(sql);
        sb.append(" limit ");
        if (page < 1) {
            page = 1;
        }
        sb.append(((page - 1) * rows)).append(",");
        sb.append(rows);
        List<Map<String, Object>> lmap;
        if (!attrs.isEmpty()) {
            sb.append(" where ");
            sb.append(condsMap2Str(attrs));
            lmap = queryRunner.query(sb.toString(), new MapListHandler(), getMapValues(attrs));
        } else {
            lmap = queryRunner.query(sb.toString(), new MapListHandler());
        }
        if (dev) {
            log.log(Level.FINER, sb.toString());
        }
        return lmap;
    }

    @Override
    public boolean execute(String sql, Object... params) throws SQLException {
        return queryRunner.update(sql, params) > 0;
    }

    @Override
    public boolean execCellStateMent(String sql, Object... params) {
        return false;
    }

    @Override
    public List<Map<String, Object>> queryListWithParams(String sql,
                                                         Object... params) throws SQLException {
        return queryRunner.query(sql, new MapListHandler(), params);
    }

    @Override
    public Object queryFirstObj(String sql, Object... params) throws SQLException {
        return queryRunner.query(sql, new ScalarHandler<>(1), params);
    }

    @Override
    public Map<String, Object> queryFirstWithParams(String sql, Object... params)
            throws SQLException {
        return queryRunner.query(sql, new MapHandler(), params);
    }

    @Override
    public Map<String, Object> loadById(Object id) {
        try {
            return set(pk, id).queryFirst(ALL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}