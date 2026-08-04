package com.zrlog.web;

import com.hibegin.common.util.EnvKit;
import com.hibegin.common.util.ParseArgsUtil;
import com.hibegin.http.server.WebServerBuilder;
import com.hibegin.http.server.util.PathUtil;
import com.hibegin.lambda.LambdaApplication;
import com.zrlog.common.Constants;
import com.zrlog.common.Updater;
import com.zrlog.common.exception.NotImplementException;
import com.zrlog.common.updater.ZipUpdater;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.ZrLogBaseNativeImageUtils;
import com.zrlog.web.config.ZrLogConfigImpl;
import com.zrlog.web.util.UpdaterUtils;

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
        EnvKit.enableLoggingToFile();
        if (EnvKit.isLambda()) {
            LambdaApplication.initLambdaEnv();
        }
    }

    static void initZrLogEnv() {
        String home = getZrLogHome();
        if (Objects.isNull(home)) {
            return;
        }
        PathUtil.setRootPath(home);
    }

    public static void main(String[] args) throws Exception {
        Application.initZrLogEnv();
        int exitCode;
        if (EnvKit.isNativeImage()) {
            exitCode = nativeStartWithExitCode(args);
        } else {
            exitCode = startWithExitCode(args);
        }
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    public static void start(String[] args) {
        startWithExitCode(args);
    }

    static int startWithExitCode(String[] args) {
        ApplicationCommandLine commandLine;
        try {
            commandLine = ApplicationCommandLine.parse(args);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return 2;
        }
        String[] serveArgs = commandLine.getServeArgs();
        if (ParseArgsUtil.justTips(serveArgs, "zrlog", BlogBuildInfoUtil.getVersionInfoFull())) {
            return 0;
        }
        if (commandLine.getCommand() == ApplicationCommandLine.Command.UPGRADE) {
            return ApplicationUpgradeRunner.run(UpdaterUtils.getUpdater(serveArgs, null), commandLine.isPreview());
        }
        ApplicationStartupOptions options = ApplicationStartupOptions.parse(serveArgs);
        webServerBuilder(options.getPort(), options.getContextPath(), UpdaterUtils.getUpdater(serveArgs, null)).start();
        return 0;
    }

    static void nativeStart(String[] args) throws Exception {
        nativeStartWithExitCode(args);
    }

    static int nativeStartWithExitCode(String[] args) throws Exception {
        ApplicationCommandLine commandLine;
        try {
            commandLine = ApplicationCommandLine.parse(args);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return 2;
        }
        String[] serveArgs = commandLine.getServeArgs();
        ApplicationStartupOptions options = ApplicationStartupOptions.parse(serveArgs);
        File execFile = new File(ZrLogBaseNativeImageUtils.getExecFile());
        if (ParseArgsUtil.justTips(serveArgs, execFile.getName(), BlogBuildInfoUtil.getVersionInfoFull())) {
            return 0;
        }
        if (commandLine.getCommand() == ApplicationCommandLine.Command.UPGRADE) {
            return ApplicationUpgradeRunner.run(UpdaterUtils.getUpdater(serveArgs, execFile), commandLine.isPreview());
        }
        if (EnvKit.isFaaSMode()) {
            WebServerBuilder webServerBuilder = webServerBuilder(options.getPort(), options.getContextPath(),
                    UpdaterUtils.getUpdater(serveArgs, execFile));
            webServerBuilder.startInBackground();
            if (EnvKit.isLambda()) {
                LambdaApplication.startHandle(Constants.zrLogConfig);
                return 0;
            }
            throw new NotImplementException();
        }
        WebServerBuilder webServerBuilder = webServerBuilder(options.getPort(), options.getContextPath(),
                UpdaterUtils.getUpdater(serveArgs, execFile));
        webServerBuilder.start();
        return 0;
    }

    public static WebServerBuilder webServerBuilder(int port, String contextPath, Updater updater) {
        ZrLogConfigImpl zrLogConfig = new ZrLogConfigImpl(port, updater, contextPath);
        Constants.zrLogConfig = zrLogConfig;
        WebServerBuilder builder = new WebServerBuilder.Builder().config(zrLogConfig).build();
        zrLogConfig.setServerBuilder(builder);
        zrLogConfig.getServerConfig().addCreateErrorHandle(() -> {
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
