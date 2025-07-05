package com.zrlog.business.service;

import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.LoggerUtil;
import com.hibegin.common.util.StringUtils;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.business.version.UpgradeVersionHandler;
import com.zrlog.common.Constants;
import com.zrlog.util.ZrLogUtil;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DbUpgradeService {

    private static final Logger LOGGER = LoggerUtil.getLogger(DbUpgradeService.class);

    private final DataSource dataSource;

    public DbUpgradeService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Long getCurrentSqlVersion(Connection connection) {
        if (Objects.isNull(connection)) {
            return -1L;
        }
        try {
            String queryVersionSQL = "select value from website where name = ?";
            try (PreparedStatement ps = connection.prepareStatement(queryVersionSQL)) {
                ps.setString(1, Constants.ZRLOG_SQL_VERSION_KEY);
                try (ResultSet resultSet = ps.executeQuery()) {
                    if (resultSet.next()) {
                        return Long.parseLong(resultSet.getString(1));
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DB connect error ", e);
        }
        return -1L;
    }

    private static Map<Integer, String> getSqlFileList() {
        Map<Integer, String> fileList = new LinkedHashMap<>();
        for (int i = 1; i <= Constants.SQL_VERSION; i++) {
            InputStream sqlStream = PathUtil.getConfInputStream("/update-sql/" + i + ".sql");
            if (Objects.nonNull(sqlStream)) {
                fileList.put(i, IOUtil.getStringInputStream(sqlStream));
            }

        }
        return fileList;
    }

    private static List<Map.Entry<Integer, List<String>>> getExecSqlList(Long dbVersion) {
        List<Map.Entry<Integer, List<String>>> sqlList = new ArrayList<>();

        for (Map.Entry<Integer, String> f : getSqlFileList().entrySet()) {
            int fileVersion = f.getKey();
            if (fileVersion > dbVersion) {
                Map.Entry<Integer, List<String>> entry = new AbstractMap.SimpleEntry<>(fileVersion, ZrLogUtil.extractExecutableSql(f.getValue()));
                LOGGER.info("Need update sql " + fileVersion + ".sql \n" + String.join(";\n", entry.getValue()) + ";");
                sqlList.add(entry);
            }
        }
        return sqlList;
    }

    /**
     * 检查数据文件是否需要更新
     * 为了处理由于数据库表的更新，导致系统无法正常使用的情况，通过执行/conf/update-sql/目录下面的*.sql文件来变更数据库的表格式，
     * 来达到系统无需手动执行数据库脚本文件。
     */
    public int tryDoUpgrade() {
        try {
            Connection connection = dataSource.getConnection();
            if (Objects.isNull(connection)) {
                return -1;
            }
            LOGGER.info("Db connect success " + connection.getCatalog());
            Long currentVersion = getCurrentSqlVersion(connection);
            if (Objects.isNull(currentVersion) || currentVersion < 0) {
                return -1;
            }
            List<Map.Entry<Integer, List<String>>> sqlList = getExecSqlList(currentVersion);
            if (sqlList.isEmpty()) {
                return -1;
            }

            for (Map.Entry<Integer, List<String>> entry : sqlList) {
                //执行需要更新的sql脚本
                try (Statement statement = connection.createStatement()) {
                    for (String sql : entry.getValue()) {
                        if (StringUtils.isEmpty(sql.trim())) {
                            continue;
                        }
                        statement.execute(sql);
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "execution sql ", e);
                    //有异常终止升级
                    return -1;
                }
                //执行需要转换的数据
                try {
                    UpgradeVersionHandler upgradeVersionHandler = (UpgradeVersionHandler) Class.forName("com.zrlog.business.version.V" + entry.getKey() + "UpgradeVersionHandler").getDeclaredConstructor().newInstance();
                    try {
                        upgradeVersionHandler.doUpgrade(connection);
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "", e);
                        return -1;
                    }
                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                    if (Constants.debugLoggerPrintAble()) {
                        LOGGER.log(Level.WARNING, "Try exec upgrade method error, " + e.getMessage());
                    }
                }
            }
            return Constants.SQL_VERSION;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "", e);
            return -1;
        }
    }
}
