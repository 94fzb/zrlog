package com.zrlog.web.support;

import com.google.gson.Gson;
import com.hibegin.common.dao.DAO;
import com.hibegin.common.dao.DataSourceWrapper;
import com.zrlog.common.Constants;
import com.zrlog.common.cache.vo.BaseDataInitVO;
import com.zrlog.common.vo.PublicWebSiteInfo;
import com.zrlog.util.DataSourceUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Properties;
import java.util.UUID;

public class InMemoryZrLogDatabase implements AutoCloseable {

    private final DataSourceWrapper previousDataSource;
    private final DataSourceWrapper dataSource;
    private final File rootPath;
    private final Properties properties;

    private InMemoryZrLogDatabase(File rootPath) throws Exception {
        this.previousDataSource = currentDefaultDataSource();
        this.rootPath = rootPath;
        this.properties = newProperties();
        writeInstallFiles();
        this.dataSource = DataSourceUtil.buildDataSource(properties);
        loadSchema();
        seedInstalledCache();
    }

    public static InMemoryZrLogDatabase open(File rootPath) throws Exception {
        return new InMemoryZrLogDatabase(rootPath);
    }

    private Properties newProperties() {
        Properties props = new Properties();
        props.setProperty("driverClass", "org.h2.Driver");
        props.setProperty("jdbcUrl", "jdbc:h2:mem:/zrlog_web_" + UUID.randomUUID()
                + ";MODE=MySQL;DATABASE_TO_UPPER=false;CASE_INSENSITIVE_IDENTIFIERS=TRUE"
                + ";NON_KEYWORDS=USER,VALUE,COMMENT,TYPE;DB_CLOSE_DELAY=-1");
        props.setProperty("user", "sa");
        props.setProperty("password", "");
        return props;
    }

    private void writeInstallFiles() throws Exception {
        File conf = new File(rootPath, "conf");
        if (!conf.exists() && !conf.mkdirs()) {
            throw new IllegalStateException("Cannot create conf directory " + conf);
        }
        File lockFile = new File(conf, "install.lock");
        if (!lockFile.exists() && !lockFile.createNewFile()) {
            throw new IllegalStateException("Cannot create install lock " + lockFile);
        }
        try (FileOutputStream output = new FileOutputStream(new File(conf, "db.properties"))) {
            properties.store(output, "zrlog-web h2 test database");
        }
    }

    private void loadSchema() throws Exception {
        try (InputStream input = InMemoryZrLogDatabase.class.getResourceAsStream("/init-table-structure.sql")) {
            if (input == null) {
                throw new IllegalStateException("Missing init-table-structure.sql from zrlog-install-web dependency");
            }
            String sql = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            for (String statement : normalizeInstallSqlForH2(sql).split(";")) {
                String trimmed = normalizeStatement(statement);
                if (!trimmed.isEmpty()) {
                    dataSource.getQueryRunner().update(trimmed);
                }
            }
        }
    }

    private void seedInstalledCache() throws SQLException {
        PublicWebSiteInfo publicInfo = new PublicWebSiteInfo();
        publicInfo.setTitle("Installed H2 Site");
        publicInfo.setHost("localhost:19085");
        publicInfo.setLanguage(Constants.DEFAULT_LANGUAGE);
        publicInfo.setSession_timeout(3600L);

        BaseDataInitVO init = new BaseDataInitVO();
        init.setWebSite(publicInfo);
        init.setWebSiteVersion(1L);

        update("insert into website(name, value, remark) values(?, ?, ?)",
                "base_data_init_cache_v3", new Gson().toJson(init), "");
        update("insert into website(name, value, remark) values(?, ?, ?)",
                "session_timeout", "3600", "");
        update("insert into website(name, value, remark) values(?, ?, ?)",
                "language", Constants.DEFAULT_LANGUAGE, "");
    }

    private void update(String sql, Object... params) throws SQLException {
        dataSource.getQueryRunner().update(sql, params);
    }

    private static String normalizeInstallSqlForH2(String sql) {
        StringBuilder builder = new StringBuilder();
        for (String line : sql.split("\\R")) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#") || trimmed.startsWith("/*!")) {
                continue;
            }
            String normalizedLine = line
                    .replaceAll("(?i)UNIQUE\\s+KEY\\s+`[^`]+`\\s*\\(", "UNIQUE (")
                    .replaceAll("(?i)KEY\\s+`[^`]+`\\s*\\(", "INDEX (")
                    .replaceAll("(?i)\\s+COMMENT\\s+'[^']*'", "");
            builder.append(normalizedLine).append('\n');
        }
        return builder.toString()
                .replace("bit(1)", "boolean")
                .replace("DEFAULT b'0'", "DEFAULT false")
                .replace("DEFAULT b'1'", "DEFAULT true")
                .replaceAll("(?i)\\)\\s*ENGINE\\s*=\\s*InnoDB\\s+DEFAULT\\s+CHARSET\\s*=\\s*[^\\s;]+"
                        + "(?:\\s+COLLATE\\s+[^\\s;]+)?", ")");
    }

    private static String normalizeStatement(String statement) {
        String trimmed = statement.trim();
        if (trimmed.toLowerCase().startsWith("drop table if exists") && trimmed.contains(",")) {
            return "";
        }
        return trimmed;
    }

    private static DataSourceWrapper currentDefaultDataSource() throws Exception {
        Field field = DAO.class.getDeclaredField("defaultDataSource");
        field.setAccessible(true);
        return (DataSourceWrapper) field.get(null);
    }

    @Override
    public void close() throws Exception {
        try {
            dataSource.close();
        } finally {
            DAO.setDs(previousDataSource);
        }
    }
}
