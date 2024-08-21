package com.hibegin.dao;

import org.apache.commons.dbutils.QueryRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class CustomQueryRunner extends QueryRunner {

    public CustomQueryRunner(DataSource dataSource, boolean b) {
        super(dataSource, b);
    }

    @Override
    public int update(Connection conn, String sql) throws SQLException {
        return this.update(conn, sql, (Object[]) null);
    }

    @Override
    public int update(Connection conn, String sql, Object param) throws SQLException {
        return this.update(conn, sql, new Object[]{param});

    }

    @Override
    public int update(Connection conn, String sql, Object... params) throws SQLException {
        if (conn == null) {
            throw new SQLException("Null connection");
        }

        if (sql == null) {
            throw new SQLException("Null SQL statement");
        }
        conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        conn.setAutoCommit(false);
        try {
            int result = super.update(conn, sql, params);
            conn.commit();
            return result;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        }
    }
}
