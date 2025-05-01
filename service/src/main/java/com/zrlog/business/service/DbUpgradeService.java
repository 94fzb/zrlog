package com.zrlog.business.service;

import com.hibegin.common.dao.DAO;
import com.hibegin.common.dao.DataSourceWrapperImpl;
import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.LoggerUtil;
import com.hibegin.common.util.StringUtils;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.business.util.SqlConvertUtils;
import com.zrlog.business.version.UpgradeVersionHandler;
import com.zrlog.common.CacheService;
import com.zrlog.common.Constants;
import com.zrlog.model.WebSite;

import java.io.InputStream;
import java.net.URI;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DbUpgradeService {
    public static final Integer SQL_VERSION = 19;


    private static final Logger LOGGER = LoggerUtil.getLogger(DbUpgradeService.class);

    private final DAO dao;
    private final WebSite webSite;
    private final String dbName;
    private final long currentSqlVersion;

    public DbUpgradeService(DataSourceWrapperImpl dataSource,
                            long currentSqlVersion) {
        this.dao = new DAO(dataSource);
        this.webSite = new WebSite(dataSource);
        URI uri = URI.create(dataSource.getDataSourceProperties().getProperty("jdbcUrl").replace("jdbc:", ""));
        this.dbName = uri.getPath().substring(1);
        this.currentSqlVersion = currentSqlVersion;
    }


    private static Map<Integer, String> getSqlFileList() {
        Map<Integer, String> fileList = new LinkedHashMap<>();
        for (int i = 1; i <= SQL_VERSION; i++) {
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
                Map.Entry<Integer, List<String>> entry = new AbstractMap.SimpleEntry<>(fileVersion, SqlConvertUtils.extractExecutableSql(f.getValue()));
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
    public void tryDoUpgrade() {
        try {
            if (currentSqlVersion < 0) {
                return;
            }
            LOGGER.info("Db " + dbName + " connect success");
            List<Map.Entry<Integer, List<String>>> sqlList = getExecSqlList(currentSqlVersion);
            if (sqlList.isEmpty()) {
                return;
            }
            for (Map.Entry<Integer, List<String>> entry : sqlList) {
                //执行需要更新的sql脚本
                try {
                    for (String sql : entry.getValue()) {
                        if (StringUtils.isEmpty(sql.trim())) {
                            continue;
                        }
                        dao.execute(sql);
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "execution sql ", e);
                    //有异常终止升级
                    return;
                }
                //执行需要转换的数据
                try {
                    UpgradeVersionHandler upgradeVersionHandler = (UpgradeVersionHandler) Class.forName("com.zrlog.business.version.V" + entry.getKey() + "UpgradeVersionHandler").getDeclaredConstructor().newInstance();
                    try {
                        upgradeVersionHandler.doUpgrade(dao);
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "", e);
                        return;
                    }
                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                    if (Constants.debugLoggerPrintAble()) {
                        LOGGER.log(Level.WARNING, "Try exec upgrade method error, " + e.getMessage());
                    }
                }
            }
            webSite.updateByKV(CacheService.ZRLOG_SQL_VERSION_KEY, SQL_VERSION + "");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "", e);
        }
    }
}
