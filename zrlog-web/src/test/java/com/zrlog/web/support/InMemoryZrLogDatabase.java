package com.zrlog.web.support;

import com.google.gson.Gson;
import com.hibegin.common.dao.DataSourceWrapper;
import com.hibegin.common.dao.InMemoryDatabase;
import com.zrlog.common.Constants;
import com.zrlog.common.cache.vo.BaseDataInitVO;
import com.zrlog.common.vo.PublicWebSiteInfo;
import com.zrlog.util.DataSourceUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;
import java.util.UUID;

public class InMemoryZrLogDatabase implements AutoCloseable {

    private final InMemoryDatabase database;
    private final DataSourceWrapper dataSource;
    private final File rootPath;
    private final Properties properties;

    private InMemoryZrLogDatabase(File rootPath) throws Exception {
        this.rootPath = rootPath;
        this.properties = newProperties();
        writeInstallFiles();
        this.dataSource = DataSourceUtil.buildDataSource(properties);
        this.database = InMemoryDatabase.open(dataSource, true);
        loadSchema();
        seedInstalledCache();
    }

    public static InMemoryZrLogDatabase open(File rootPath) throws Exception {
        return new InMemoryZrLogDatabase(rootPath);
    }

    private Properties newProperties() {
        return InMemoryDatabase.h2Properties("zrlog_web_" + UUID.randomUUID());
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
            database.loadMySQLSchema(input);
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

    @Override
    public void close() {
        database.close();
    }
}
