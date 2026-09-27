package com.zrlog.web.support;

import com.hibegin.common.dao.InMemoryDatabase;
import com.zrlog.common.Constants;
import com.zrlog.install.business.service.InstallService;
import com.zrlog.install.business.vo.InstallConfigVO;
import com.zrlog.install.business.vo.InstallDatabaseConfig;
import com.zrlog.install.business.vo.InstallSiteConfig;
import com.zrlog.install.web.InstallAction;
import com.zrlog.install.web.config.DefaultInstallAction;
import com.zrlog.install.web.config.DefaultInstallConfig;
import com.zrlog.test.support.MemoryRuntime;

import java.io.File;
import java.util.Map;
import java.util.UUID;

/** Starts configuration tests from real installer output instead of synthetic lock/config files. */
public final class InstalledTestSite implements AutoCloseable {

    private final InMemoryDatabase database;

    private InstalledTestSite(File root) throws Exception {
        String name = "zrlog_web_" + UUID.randomUUID();
        var properties = InMemoryDatabase.h2Properties(name);
        InstallDatabaseConfig db = new InstallDatabaseConfig();
        db.setDbType("h2");
        db.setDbName(name);
        db.setDriverClass(properties.getProperty("driverClass"));
        db.setJdbcUrl(properties.getProperty("jdbcUrl"));
        db.setUser(properties.getProperty("user"));
        db.setPassword(properties.getProperty("password"));
        InstallSiteConfig site = new InstallSiteConfig();
        site.setTitle("Installed H2 Site");
        site.setUsername("admin");
        site.setPassword("local-test-password");
        site.setEmail("admin@example.com");
        site.setSecretKey("installed-test-site-secret");
        InstallConfigVO request = new InstallConfigVO();
        request.setDbConfig(db);
        request.setConfigMsg(site);
        request.setAppendWebsite(Map.of("host", "localhost:19085", "session_timeout", "3600",
                "language", Constants.DEFAULT_LANGUAGE));
        var config = new DefaultInstallConfig() {
            @Override public File getDbPropertiesFile() { return new File(root, "conf/db.properties"); }
            @Override public String defaultTemplatePath() { return Constants.getDefaultTemplatePath(); }
            @Override public InstallAction getAction() {
                return new DefaultInstallAction() {
                    @Override public File getLockFile() { return new File(root, "conf/install.lock"); }
                };
            }
        };
        if (!new InstallService(config, request).install()) {
            throw new IllegalStateException("Install test site failed");
        }
        database = InMemoryDatabase.open(MemoryRuntime.readDatabaseProperties(root.toPath()), true);
    }

    public static InstalledTestSite open(File root) throws Exception { return new InstalledTestSite(root); }

    @Override public void close() { database.close(); }
}
