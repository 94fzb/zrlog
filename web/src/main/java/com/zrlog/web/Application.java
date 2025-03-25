package com.zrlog.web;

import com.hibegin.common.util.LoggerUtil;
import com.hibegin.common.util.ParseArgsUtil;
import com.hibegin.http.server.WebServerBuilder;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.business.service.JarUpdater;
import com.zrlog.common.Constants;
import com.zrlog.common.Updater;
import com.zrlog.common.type.RunMode;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.ZrLogUtil;
import com.zrlog.web.config.ZrLogConfigImpl;

import java.io.File;
import java.net.URISyntaxException;
import java.sql.SQLRecoverableException;
import java.util.Objects;

public class Application {

    static {
        initZrLogEnv();
    }

    public static void initZrLogEnv() {
        String home = System.getenv().get("ZRLOG_HOME");
        if (Objects.nonNull(home)) {
            PathUtil.setRootPath(home);
        }
        System.getProperties().put("java.util.logging.SimpleFormatter.format", "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$s %5$s%6$s%n");
    }

    private static void initEnv() throws URISyntaxException {
        String programDir = new File(Application.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
        //对应的开发模式
        if (programDir.contains("web/target/class") || programDir.contains("web\\target\\class")) {
            programDir = new File(programDir).getParentFile().getParentFile().getParent();
        }
        Constants.runMode = programDir.endsWith(".jar") ? RunMode.JAR : RunMode.DEV;
        if (Constants.runMode == RunMode.JAR) {
            PathUtil.setRootPath(System.getProperty("user.dir"));
        } else {
            PathUtil.setRootPath(programDir);
        }
    }

    private static Updater getUpdater(String[] args) {
        if (Constants.runMode == RunMode.JAR) {
            File jarFile = new File(System.getProperty("java.class.path"));
            return new JarUpdater(args, jarFile);
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        initEnv();
        //parse tips args
        if (ParseArgsUtil.justTips(args, "zrlog", BlogBuildInfoUtil.getVersionInfoFull())) {
            return;
        }
        webServerBuilder(ZrLogUtil.getPort(args), getUpdater(args)).start();
    }

    private static void configDatabaseWithRetry(int timeoutInSeconds) throws InterruptedException {
        try {
            Constants.zrLogConfig.configDatabase();
        } catch (Exception e) {
            if (timeoutInSeconds > 0 && e instanceof SQLRecoverableException) {
                int seekSeconds = 5;
                Thread.sleep(seekSeconds * 1000);
                configDatabaseWithRetry(timeoutInSeconds - seekSeconds);
                return;
            }
            LoggerUtil.getLogger(Application.class).warning("Config database error " + e.getMessage());
            if (!Constants.zrLogConfig.getServerConfig().isNativeImageAgent()) {
                System.exit(-1);
            }
        }
    }

    public static WebServerBuilder webServerBuilder(int port, Updater updater) throws Exception {
        Constants.zrLogConfig = new ZrLogConfigImpl(port, updater);
        configDatabaseWithRetry(20);
        WebServerBuilder builder = new WebServerBuilder.Builder().config(Constants.zrLogConfig).build();
        builder.addCreateErrorHandle(() -> {
            if (updater instanceof JarUpdater) {
                Thread.sleep(1000);
                builder.start();
                return null;
            }
            System.exit(-1);
            return null;
        });
        //启动成功，更新一次缓存数据
        builder.addCreateSuccessHandle(() -> {
            Constants.zrLogConfig.startPluginsAsync();
            return null;
        });
        return builder;
    }


}
