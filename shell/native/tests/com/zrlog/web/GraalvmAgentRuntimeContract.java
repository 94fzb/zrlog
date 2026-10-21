package com.zrlog.web;

import com.hibegin.common.dao.InMemoryDatabase;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.util.DbUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;

/**
 * Runs in a separate JVM so the agent's global paths and DAO cannot affect other tests.
 */
public class GraalvmAgentRuntimeContract {

    public static void main(String[] args) throws Exception {
        String previousUrl = null;
        for (int attempt = 0; attempt < 2; attempt++) {
            Path root;
            GraalvmAgentRuntime runtime = GraalvmAgentRuntime.install();
            root = Path.of(PathUtil.getRootPath());
            Path log = root.resolve("log/held-open.log");
            try (FileChannel heldLog = FileChannel.open(log, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                 var lock = heldLog.lock();
                 GraalvmAgentRuntime ignored = runtime) {
                check(!root.equals(Path.of(args[0])), "Agent reused the existing site");
                check(Files.isRegularFile(root.resolve("conf/install.lock")), "Installer did not create install.lock");
                var properties = DbUtils.getDbProp(root.resolve("conf/db.properties").toFile());
                String url = properties.getProperty("jdbcUrl");
                check(url.startsWith("jdbc:h2:mem:"), "Agent did not use an in-memory database");
                check(!url.equals(previousUrl), "Agent reused an earlier database");
                previousUrl = url;
                try (InMemoryDatabase db = InMemoryDatabase.open(properties, false)) {
                    check("ZrLog Native Agent".equals(db.scalar("SELECT value FROM website WHERE name = 'title'")),
                            "Default site JSON was not installed");
                    check(((Number) db.scalar("SELECT count(*) FROM user WHERE userName = 'admin'")).intValue() == 1,
                            "Installer did not create the admin user");
                    check(((Number) db.scalar("SELECT count(*) FROM log")).intValue() > 0,
                            "Installer did not create the default article");
                }
            }
            check(Files.isDirectory(root), "Runtime directory was removed before the tracing process exited");
        }
        System.out.println("Native agent installation contract passed");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
