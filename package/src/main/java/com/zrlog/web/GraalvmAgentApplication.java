package com.zrlog.web;

import com.hibegin.common.dao.DataSourceWrapper;
import com.hibegin.common.util.LoggerUtil;
import com.hibegin.http.server.WebServerBuilder;
import com.hibegin.http.server.util.NativeImageUtils;
import com.hibegin.lambda.rest.ApiGatewayHttp;
import com.hibegin.lambda.rest.ApiGatewayRequestContext;
import com.hibegin.lambda.rest.LambdaApiGatewayRequest;
import com.hibegin.lambda.rest.LambdaApiGatewayResponse;
import com.zrlog.common.Constants;
import com.zrlog.common.ZrLogConfig;
import com.zrlog.util.ArgsParser;
import com.zrlog.util.BlogBuildInfoUtil;

import java.util.Arrays;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * 该类仅提供基础的 graalvm native image agent 运行依赖反射扫描等功能，不作为实际的启动入口
 */
public class GraalvmAgentApplication {

    private static final Logger LOGGER = Logger.getLogger(GraalvmAgentApplication.class.getName());

    static {
        Constants.init();
    }

    private static void registerLambdaJsonTypes() {
        NativeImageUtils.gsonNativeAgentByClazz(Arrays.asList(LambdaApiGatewayRequest.class, ApiGatewayHttp.class,
                ApiGatewayRequestContext.class, LambdaApiGatewayResponse.class));
    }

    public static void main(String[] args) throws Exception {
        ZrLogConfig.nativeImageAgent = true;
        BlogBuildInfoUtil.getBlogProp();
        try (GraalvmAgentRuntime runtime = GraalvmAgentRuntime.install()) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    runtime.close();
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Clean native agent runtime failed", e);
                }
            }, "native-agent-cleanup"));
            registerLambdaJsonTypes();
            logWebSetupProviders();
            startWebServer(args, runtime);
        }
    }

    private static void logWebSetupProviders() {
        List<String> providerNames = ServiceLoader.load(WebSetupProvider.class).stream()
                .map(provider -> provider.type().getName())
                .collect(Collectors.toList());
        LOGGER.info("WebSetup SPI providers: " + providerNames);
    }

    private static void startWebServer(String[] args, GraalvmAgentRuntime runtime) {
        WebServerBuilder webServerBuilder = Application.webServerBuilder(0, ArgsParser.getContextPath(args), null);
        Constants.zrLogConfig.getServerConfig().setDisableSavePidFile(true);
        Constants.zrLogConfig.getServerConfig().setServerPortFilePathEnvKey(null);
        Constants.zrLogConfig.getServerConfig().addCreateSuccessHandle(() -> {
            // Agent sampling is complete; application shutdown hooks can wait on runtime services indefinitely.
            try {
                ((DataSourceWrapper) Constants.zrLogConfig.getDataSource()).close();
                if (LoggerUtil.getFileHandler() != null) {
                    LoggerUtil.getFileHandler().close();
                }
                runtime.close();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Clean native agent runtime failed", e);
                Runtime.getRuntime().halt(1);
            }
            Runtime.getRuntime().halt(0);
            return null;
        });
        webServerBuilder.start();
    }
}
