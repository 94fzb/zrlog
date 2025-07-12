package com.zrlog.data;

import com.hibegin.common.dao.DataSourceWrapperImpl;
import com.hibegin.common.util.EnvKit;
import com.zrlog.common.Constants;

import java.util.Properties;

public class DataSourceUtil {

    public static DataSourceWrapperImpl buildDataSource(Properties dbProperties) {
        /*if (EnvKit.isLambda()) {
            //Lambda 不用连接池
            return new DriverDataSource(dbProperties.getProperty("jdbcUrl"),
                    dbProperties.getProperty("driverClass"),
                    dbProperties,
                    dbProperties.getProperty("user"),
                    dbProperties.getProperty("password"));
        } else {*/
        DataSourceWrapperImpl dataSource = new DataSourceWrapperImpl(dbProperties, Constants.debugLoggerPrintAble());
        if (!dataSource.isWebApi()) {
            dataSource.setDriverClassName(dbProperties.getProperty("driverClass"));
            dataSource.setJdbcUrl(dbProperties.getProperty("jdbcUrl"));
        }
        dataSource.setUsername(dbProperties.getProperty("user"));
        dataSource.setPassword(dbProperties.getProperty("password"));
        if (EnvKit.isFaaSMode()) {
            dataSource.setMinimumIdle(5);
            dataSource.setIdleTimeout(5000);
            dataSource.setKeepaliveTime(2000);
        }
        return dataSource;
        /*}*/
    }
}
