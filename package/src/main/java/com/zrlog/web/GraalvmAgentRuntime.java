package com.zrlog.web;

import com.google.gson.Gson;
import com.hibegin.common.dao.InMemoryDatabase;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.business.version.UpgradeVersionHandler;
import com.zrlog.common.Constants;
import com.zrlog.install.business.service.InstallService;
import com.zrlog.install.business.vo.InstallConfigVO;
import com.zrlog.install.business.vo.InstallDatabaseConfig;
import com.zrlog.install.web.InstallAction;
import com.zrlog.install.web.InstallConstants;
import com.zrlog.install.web.config.DefaultInstallAction;
import com.zrlog.install.web.config.DefaultInstallConfig;

import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Stream;

/** A disposable installed site used only by the Native Image tracing agent. */
final class GraalvmAgentRuntime implements AutoCloseable {

    private static final Logger LOGGER = Logger.getLogger(GraalvmAgentRuntime.class.getName());
    private final Path root;
    private final InMemoryDatabase database;
    private boolean closed;

    private GraalvmAgentRuntime(Path root, InMemoryDatabase database) {
        this.root = root;
        this.database = database;
    }

    static GraalvmAgentRuntime install() throws Exception {
        // Direct launches must also fail before any inherited database can be opened.
        requireEnvironment("DB_PROPERTIES", "");
        requireEnvironment("DB_PROPERTIES_FILE_NAME", "db.properties");
        requireEnvironment("INSTALL_LOCK_FILE_NAME", "install.lock");
        Path root = Files.createTempDirectory("zrlog-native-agent-");
        InMemoryDatabase database = null;
        try {
            System.setProperty("sws.root.path", root.toString());
            PathUtil.setRootPath(root.toString());
            for (String directory : new String[]{"conf", "static", "cache", "temp", "log"}) {
                Path path = Files.createDirectories(root.resolve(directory));
                System.setProperty("sws." + directory + ".path", path.toString());
            }
            InstallConfigVO request;
            try (var input = GraalvmAgentRuntime.class.getResourceAsStream("/native-agent/default-install.json")) {
                if (input == null) {
                    throw new IllegalStateException("Missing native agent default install configuration");
                }
                request = new Gson().fromJson(new InputStreamReader(input, StandardCharsets.UTF_8), InstallConfigVO.class);
            }
            String name = "zrlog_native_agent_" + UUID.randomUUID();
            Properties properties = InMemoryDatabase.h2Properties(name);
            InstallDatabaseConfig db = new InstallDatabaseConfig();
            db.setDriverClass(properties.getProperty("driverClass"));
            db.setJdbcUrl(properties.getProperty("jdbcUrl"));
            db.setUser(properties.getProperty("user"));
            db.setPassword(properties.getProperty("password"));
            db.setDbType("h2");
            db.setDbName(name);
            request.setDbConfig(db);
            var config = new DefaultInstallConfig() {
                @Override public File getDbPropertiesFile() { return root.resolve("conf/db.properties").toFile(); }
                @Override public String defaultTemplatePath() { return Constants.getDefaultTemplatePath(); }
                @Override public String getZrLogSqlVersion() { return String.valueOf(UpgradeVersionHandler.SQL_VERSION); }
                @Override public InstallAction getAction() {
                    return new DefaultInstallAction() {
                        @Override public File getLockFile() { return root.resolve("conf/install.lock").toFile(); }
                    };
                }
            };
            var previousConfig = InstallConstants.installConfig;
            try {
                InstallConstants.installConfig = config;
                if (!new InstallService(config, request).install()) {
                    throw new IllegalStateException("Install native agent memory database failed");
                }
            } finally {
                InstallConstants.installConfig = previousConfig;
            }
            database = InMemoryDatabase.open(properties, true);
            LOGGER.info("Native agent installed disposable H2 site at " + root);
            return new GraalvmAgentRuntime(root, database);
        } catch (Exception e) {
            try {
                if (database != null) {
                    database.close();
                }
            } catch (Exception cleanupError) {
                e.addSuppressed(cleanupError);
            }
            try {
                delete(root);
            } catch (Exception cleanupError) {
                e.addSuppressed(cleanupError);
            }
            throw e;
        }
    }

    private static void requireEnvironment(String name, String expected) {
        String value = System.getenv(name);
        if (value != null && !value.equals(expected)) {
            throw new IllegalStateException("Native agent requires an isolated " + name + "; use exec:exec@java-agent -Dagent");
        }
    }

    private static void delete(Path root) throws Exception {
        try (Stream<Path> paths = Files.walk(root)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toArray(Path[]::new)) {
                Files.deleteIfExists(path);
            }
        }
    }

    @Override
    public synchronized void close() throws Exception {
        if (closed) {
            return;
        }
        try {
            database.close();
        } finally {
            delete(root);
            closed = true;
        }
    }
}
