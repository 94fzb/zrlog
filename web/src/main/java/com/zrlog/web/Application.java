package com.zrlog.web;

import com.hibegin.common.util.ParseArgsUtil;
import com.hibegin.http.server.WebServerBuilder;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.business.service.JarUpdater;
import com.zrlog.common.Constants;
import com.zrlog.common.Updater;
import com.zrlog.common.ZrLogConfig;
import com.zrlog.common.type.RunMode;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.ZrLogUtil;
import com.zrlog.web.config.ZrLogConfigImpl;

import java.io.File;
import java.net.URISyntaxException;

public class Application {

    static {
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
            return new JarUpdater(args, jarFile.getName());
        }
        return null;
    }

    public static void main(String[] args) throws URISyntaxException {
        initEnv();
        //parse tips args
        if (ParseArgsUtil.justTips(args, "zrlog", BlogBuildInfoUtil.getVersionInfoFull())) {
            return;
        }
        webServerBuilder(ZrLogUtil.getPort(args), getUpdater(args)).start();
    }

    public static WebServerBuilder webServerBuilder(int port, Updater updater) {
        ZrLogConfig zrLogConfig = new ZrLogConfigImpl(port, updater);
        Constants.zrLogConfig = zrLogConfig;
        WebServerBuilder builder = new WebServerBuilder.Builder().config(zrLogConfig).build();
        builder.addStartErrorHandle(() -> {
            if (updater instanceof JarUpdater) {
                Thread.sleep(1000);
                builder.start();
                return null;
            }
            System.exit(-1);
            return null;
        });
        return builder;
    }


}
