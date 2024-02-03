package com.hibegin.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface IDAO {


    boolean save() throws SQLException;

    boolean update(Map<String, Object> conditions) throws SQLException;

    boolean updateById(Object id) throws SQLException;

    boolean delete() throws SQLException;

    boolean deleteById(int id) throws SQLException;

    boolean execute(String sql, Object... params) throws SQLException;

    boolean execCellStateMent(String sql, Object... params);

    Map<String, Object> loadById(Object id);

    Map<String, Object> queryFirst(String... columns) throws SQLException;

    List<Map<String, Object>> queryList(String... columns) throws SQLException;

    List<Map<String, Object>> queryListWithParams(String sql, Object... params) throws SQLException;

    Object queryFirst(String column) throws SQLException;

    Map<String, Object> queryFirstWithParams(String sql, Object... params) throws SQLException;

    Object queryFirstObj(String sql, Object... params) throws SQLException;

    List<Map<String, Object>> queryPagination(int page, int rows, String... columns) throws SQLException;

    List<Map<String, Object>> queryPagination(String sql, int page, int rows) throws SQLException;

}