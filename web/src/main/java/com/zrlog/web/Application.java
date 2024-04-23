package com.zrlog.web;

import com.hibegin.http.server.WebServerBuilder;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.admin.web.plugin.ZipUpdateVersionHandle;
import com.zrlog.common.Constants;
import com.zrlog.common.ZrLogConfig;
import com.zrlog.util.JarUpdater;
import com.zrlog.util.ZrLogUtil;
import com.zrlog.web.config.ZrLogConfigImpl;

import java.io.File;
import java.net.URISyntaxException;

public class Application {

    public static void main(String[] args) throws URISyntaxException {
        System.getProperties().put("java.util.logging.SimpleFormatter.format", "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$s %5$s%6$s%n");
        String programDir = new File(Application.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
        //对应的开发模式
        if (programDir.contains("web/target/class") || programDir.contains("web\\target\\class")) {
            programDir = new File(programDir).getParentFile().getParentFile().getParent();
        }
        boolean jarMode = programDir.endsWith(".jar");
        String starterName = "";
        if (jarMode) {
            File jarFile = new File(programDir);
            starterName = jarFile.getName();
            programDir = jarFile.getParent();
        }
        PathUtil.setRootPath(programDir);
        ZrLogConfig zrLogConfig = new ZrLogConfigImpl(ZrLogUtil.getPort(args), new JarUpdater(args, starterName));
        Constants.zrLogConfig = zrLogConfig;
        WebServerBuilder builder = new WebServerBuilder.Builder().config(zrLogConfig).build();
        builder.addStartErrorHandle(() -> {
            if (jarMode) {
                Thread.sleep(1000);
                builder.start();
                return null;
            }
            System.exit(-1);
            return null;
        });
        builder.start();
    }


}
