package com.zrlog.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

public class DbConnectUtils{
    public static Connection getConnection(Properties dbConn) throws ClassNotFoundException, SQLException {
        Class.forName(dbConn.getProperty("driverClass"));
        DriverManager.setLoginTimeout(20);
        return DriverManager.getConnection(dbConn.getProperty("jdbcUrl"), dbConn.getProperty("user"), dbConn.getProperty("password"));
    }
}