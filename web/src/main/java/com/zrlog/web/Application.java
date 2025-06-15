package com.zrlog.web;

import com.hibegin.common.util.EnvKit;
import com.hibegin.common.util.ParseArgsUtil;
import com.hibegin.http.server.WebServerBuilder;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.business.service.ZipUpdater;
import com.zrlog.business.util.UpdaterUtils;
import com.zrlog.common.Constants;
import com.zrlog.common.Updater;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.ZrLogUtil;
import com.zrlog.web.config.ZrLogConfigImpl;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Objects;

import static com.zrlog.common.Constants.getZrLogHomeByEnv;

public class Application {


    static {
        initZrLogEnv();
    }

    public static void initZrLogEnv() {
        String home = getZrLogHomeByEnv();
        if (Objects.nonNull(home)) {
            PathUtil.setRootPath(home);
        }
    }

    private static void initEnv() throws URISyntaxException {
        String programDir = new File(Application.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
        //对应的开发模式
        if (programDir.contains("web/target/class") || programDir.contains("web\\target\\class")) {
            programDir = new File(programDir).getParentFile().getParentFile().getParent();
        }
        if (EnvKit.isDevMode()) {
            PathUtil.setRootPath(programDir);
        } else {
            PathUtil.setRootPath(System.getProperty("user.dir"));
        }
    }



    public static void main(String[] args) throws Exception {
        initEnv();
        //parse tips args
        if (ParseArgsUtil.justTips(args, "zrlog", BlogBuildInfoUtil.getVersionInfoFull())) {
            return;
        }
        webServerBuilder(ZrLogUtil.getPort(args), UpdaterUtils.getZipUpdater(args)).start();
    }

    public static WebServerBuilder webServerBuilder(int port, Updater updater) throws Exception {
        Constants.zrLogConfig = new ZrLogConfigImpl(port, updater, "");
        WebServerBuilder builder = new WebServerBuilder.Builder().config(Constants.zrLogConfig).build();
        builder.addCreateErrorHandle(() -> {
            if (updater instanceof ZipUpdater) {
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
