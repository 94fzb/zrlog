package com.zrlog.web;

import com.hibegin.common.util.EnvKit;
import com.hibegin.common.util.LoggerUtil;
import com.hibegin.common.util.ParseArgsUtil;
import com.hibegin.http.server.SimpleWebServer;
import com.hibegin.http.server.WebServerBuilder;
import com.hibegin.http.server.util.PathUtil;
import com.hibegin.lambda.LambdaApplication;
import com.zrlog.common.Updater;
import com.zrlog.common.exception.NotImplementException;
import com.zrlog.common.UpdaterTypeEnum;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.ZrLogBaseNativeImageUtils;
import com.zrlog.web.config.ZrLogConfigImpl;

import java.io.File;
import java.util.Objects;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

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
        boolean debCommand = args.length > 0 && "upgrade".equals(args[0]) && "deb".equals(BlogBuildInfoUtil.getPackageType());
        if (exitCode != 0 || debCommand) {
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
        com.zrlog.util.NetworkProxy.initialize();
        if (commandLine.getCommand() == ApplicationCommandLine.Command.UPGRADE) {
            return ApplicationUpgradeRunner.run(ApplicationUpdaterFactory.create(serveArgs, null), commandLine.isPreview(), commandLine.isJson());
        }
        ApplicationStartupOptions options = ApplicationStartupOptions.parse(serveArgs);
        webServerBuilder(options.getPort(), options.getContextPath(), ApplicationUpdaterFactory.create(serveArgs, null)).start();
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
        File execFile = new File(ZrLogBaseNativeImageUtils.getExecFile());
        if (ParseArgsUtil.justTips(serveArgs, execFile.getName(), BlogBuildInfoUtil.getVersionInfoFull())) {
            return 0;
        }
        com.zrlog.util.NetworkProxy.initialize();
        if (commandLine.getCommand() == ApplicationCommandLine.Command.UPGRADE) {
            return ApplicationUpgradeRunner.run(ApplicationUpdaterFactory.create(serveArgs, execFile), commandLine.isPreview(), commandLine.isJson());
        }
        ApplicationStartupOptions options = ApplicationStartupOptions.parse(serveArgs);
        if (EnvKit.isFaaSMode()) {
            ZrLogConfigImpl config = new ZrLogConfigImpl(options.getPort(),
                    ApplicationUpdaterFactory.create(serveArgs, execFile), options.getContextPath());
            WebServerBuilder webServerBuilder = webServerBuilder(config);
            webServerBuilder.startInBackground();
            if (EnvKit.isLambda()) {
                LambdaApplication.startHandle(config);
                return 0;
            }
            throw new NotImplementException();
        }
        WebServerBuilder webServerBuilder = webServerBuilder(options.getPort(), options.getContextPath(),
                ApplicationUpdaterFactory.create(serveArgs, execFile));
        webServerBuilder.start();
        return 0;
    }

    public static WebServerBuilder webServerBuilder(int port, String contextPath, Updater updater) {
        return webServerBuilder(new ZrLogConfigImpl(port, updater, contextPath));
    }

    public static WebServerBuilder webServerBuilder(ZrLogConfigImpl zrLogConfig) {
        WebServerBuilder builder = new WebServerBuilder.Builder().config(zrLogConfig).build();
        zrLogConfig.setServerBuilder(builder);
        zrLogConfig.getServerConfig().addCreateSuccessHandle(() -> {
            detachFileHandlerFromWebServerShutdownLogger();
            return null;
        });
        zrLogConfig.getServerConfig().addCreateErrorHandle(() -> {
            Updater updater = zrLogConfig.getUpdater();
            if (updater != null && updater.getType() == UpdaterTypeEnum.ZIP) {
                Thread.sleep(1000);
                builder.start();
                return null;
            }
            System.exit(-1);
            return null;
        });
        return builder;
    }

    static void detachFileHandlerFromWebServerShutdownLogger() {
        FileHandler fileHandler = LoggerUtil.getFileHandler();
        if (Objects.nonNull(fileHandler)) {
            Logger.getLogger(SimpleWebServer.class.getName()).removeHandler(fileHandler);
        }
    }
}
