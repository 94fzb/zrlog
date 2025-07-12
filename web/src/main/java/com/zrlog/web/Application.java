package com.zrlog.web;

import com.hibegin.common.util.EnvKit;
import com.hibegin.common.util.ParseArgsUtil;
import com.hibegin.http.server.WebServerBuilder;
import com.hibegin.http.server.util.PathUtil;
import com.hibegin.lambda.LambdaApplication;
import com.zrlog.admin.web.plugin.NativeImageUpdater;
import com.zrlog.admin.web.plugin.ZipUpdater;
import com.zrlog.common.Constants;
import com.zrlog.common.Updater;
import com.zrlog.common.type.RunMode;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.ZrLogUtil;
import com.zrlog.web.config.UpdaterUtils;
import com.zrlog.web.config.ZrLogConfigImpl;
import com.zrlog.web.util.ZrLogNativeImageUtils;

import java.io.File;
import java.util.Objects;

import static com.zrlog.common.Constants.getZrLogHome;

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

    public static void start(String[] args) throws Exception {
        //parse tips args
        if (ParseArgsUtil.justTips(args, "zrlog", BlogBuildInfoUtil.getVersionInfoFull())) {
            return;
        }
        webServerBuilder(ZrLogUtil.getPort(args), ZrLogUtil.getContextPath(args), UpdaterUtils.getZipUpdater(args)).start();
    }

    /**
     * graalvm 实际的启动入口，开发阶段不使用这个方法启动
     */
    private static void nativeStart(String[] args) throws Exception {
        Constants.runMode = RunMode.NATIVE;
        int port = ZrLogUtil.getPort(args);
        if (EnvKit.isLambda()) {
            File file = new File(ZrLogUtil.getLambdaRoot() + "/zrlog");
            WebServerBuilder webServerBuilder = webServerBuilder(port, ZrLogUtil.getContextPath(args), new NativeImageUpdater(args, file));
            webServerBuilder.startInBackground();
            LambdaApplication.startHandle(Constants.zrLogConfig);
            return;
        }
        String execFile = ZrLogNativeImageUtils.getExecFile();
        //parse args
        if (ParseArgsUtil.justTips(args, new File(execFile).getName(), BlogBuildInfoUtil.getVersionInfoFull())) {
            return;
        }
        WebServerBuilder webServerBuilder = webServerBuilder(port, ZrLogUtil.getContextPath(args), new NativeImageUpdater(args, new File(execFile)));
        webServerBuilder.start();
    }

    public static WebServerBuilder webServerBuilder(int port, String contextPath, Updater updater) {
        Constants.zrLogConfig = new ZrLogConfigImpl(port, updater, contextPath);
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
