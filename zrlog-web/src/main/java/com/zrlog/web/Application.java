package com.zrlog.web;

import com.hibegin.common.util.EnvKit;
import com.hibegin.common.util.ParseArgsUtil;
import com.hibegin.http.server.WebServerBuilder;
import com.hibegin.http.server.util.PathUtil;
import com.hibegin.lambda.LambdaApplication;
import com.zrlog.admin.web.plugin.ZipUpdater;
import com.zrlog.common.Constants;
import com.zrlog.common.Updater;
import com.zrlog.common.exception.NotImplementException;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.ZrLogBaseNativeImageUtils;
import com.zrlog.util.ZrLogUtil;
import com.zrlog.web.util.UpdaterUtils;
import com.zrlog.web.config.ZrLogConfigImpl;

import java.io.File;
import java.util.Objects;

import static com.zrlog.common.Constants.getZrLogHome;

/**
 * 实际的启动入口，开发阶段不使用这个类启动，使用对应的 package 模块下的对应的启动方式
 * JakartaServletDevApplication， 内嵌 web 容器方式（war）
 * SwsDevApplication，标准的 zip 包的启动方式
 */
public class Application {


    static {
        if (EnvKit.isLambda()) {
            LambdaApplication.initLambdaEnv();
        }
    }

    private static void initZrLogEnv() {
        String home = getZrLogHome();
        if (Objects.isNull(home)) {
            return;
        }
        PathUtil.setRootPath(home);
    }

    public static void main(String[] args) throws Exception {
        Application.initZrLogEnv();
        if (EnvKit.isNativeImage()) {
            nativeStart(args);
            return;
        }
        start(args);
    }

    public static void start(String[] args) {
        //parse tips args
        if (ParseArgsUtil.justTips(args, "zrlog", BlogBuildInfoUtil.getVersionInfoFull())) {
            return;
        }
        webServerBuilder(ZrLogUtil.getPort(args), ZrLogUtil.getContextPath(args), UpdaterUtils.getUpdater(args, null)).start();
    }

    private static void nativeStart(String[] args) throws Exception {
        int port = ZrLogUtil.getPort(args);
        File execFile = new File(ZrLogBaseNativeImageUtils.getExecFile());
        if (EnvKit.isFaaSMode()) {
            WebServerBuilder webServerBuilder = webServerBuilder(port, ZrLogUtil.getContextPath(args), UpdaterUtils.getUpdater(args, execFile));
            webServerBuilder.startInBackground();
            if (EnvKit.isLambda()) {
                LambdaApplication.startHandle(Constants.zrLogConfig);
                return;
            }
            throw new NotImplementException();
        }
        //parse args
        if (ParseArgsUtil.justTips(args, execFile.getName(), BlogBuildInfoUtil.getVersionInfoFull())) {
            return;
        }
        WebServerBuilder webServerBuilder = webServerBuilder(port, ZrLogUtil.getContextPath(args), UpdaterUtils.getUpdater(args, execFile));
        webServerBuilder.start();
    }

    public static WebServerBuilder webServerBuilder(int port, String contextPath, Updater updater) {
        Constants.zrLogConfig = new ZrLogConfigImpl(port, updater, contextPath);
        WebServerBuilder builder = new WebServerBuilder.Builder().config(Constants.zrLogConfig).build();
        Constants.zrLogConfig.getServerConfig().addCreateErrorHandle(() -> {
            if (updater instanceof ZipUpdater) {
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
