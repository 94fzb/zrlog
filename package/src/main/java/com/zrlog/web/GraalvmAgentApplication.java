package com.zrlog.web;

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
import com.zrlog.web.config.ZrLogConfigImpl;

import java.util.Arrays;
import java.util.List;
import java.util.ServiceLoader;
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
            registerLambdaJsonTypes();
            logWebSetupProviders();
            startWebServer(args);
        }
    }

    private static void logWebSetupProviders() {
        List<String> providerNames = ServiceLoader.load(WebSetupProvider.class).stream()
                .map(provider -> provider.type().getName())
                .collect(Collectors.toList());
        LOGGER.info("WebSetup SPI providers: " + providerNames);
    }

    private static void startWebServer(String[] args) {
        ZrLogConfigImpl config = new ZrLogConfigImpl(0, null, ArgsParser.getContextPath(args));
        WebServerBuilder webServerBuilder = Application.webServerBuilder(config);
        config.getServerConfig().setDisableSavePidFile(true);
        config.getServerConfig().setServerPortFilePathEnvKey(null);
        config.getServerConfig().addCreateSuccessHandle(() -> {
            // Stop owned services before halting the tracing JVM: halt skips shutdown hooks,
            // so a plugin child would otherwise retain the disposable site's files on Windows.
            config.stop();
            // The caller removes the site after this process releases its remaining logger handles.
            Runtime.getRuntime().halt(0);
            return null;
        });
        webServerBuilder.start();
    }
}
