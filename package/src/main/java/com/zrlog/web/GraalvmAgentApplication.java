package com.zrlog.web;

import com.hibegin.http.server.WebServerBuilder;
import com.hibegin.http.server.util.NativeImageUtils;
import com.hibegin.http.server.util.PathUtil;
import com.hibegin.lambda.rest.ApiGatewayHttp;
import com.hibegin.lambda.rest.ApiGatewayRequestContext;
import com.hibegin.lambda.rest.LambdaApiGatewayRequest;
import com.hibegin.lambda.rest.LambdaApiGatewayResponse;
import com.zrlog.common.Constants;
import com.zrlog.common.ZrLogConfig;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.ZrLogUtil;

import java.util.Arrays;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * 该类仅提供基础的 graalvm native image agent 运行依赖反射扫描等功能，不作为实际的启动入口
 */
public class GraalvmAgentApplication {


    private static void lambdaJson() {
        NativeImageUtils.gsonNativeAgentByClazz(Arrays.asList(LambdaApiGatewayRequest.class, ApiGatewayHttp.class,
                ApiGatewayRequestContext.class, LambdaApiGatewayResponse.class));
    }

    public static void main(String[] args) throws Exception {
        ZrLogConfig.nativeImageAgent = true;
        BlogBuildInfoUtil.getBlogProp();
        PathUtil.setRootPath(System.getProperty("user.dir").replace("\\", "/").replace("/package/target", ""));
        lambdaJson();
        logWebSetupProviders();
        //last
        webserver(args);
    }

    private static void logWebSetupProviders() {
        List<String> providerNames = ServiceLoader.load(WebSetupProvider.class).stream()
                .map(provider -> provider.type().getName())
                .collect(Collectors.toList());
        Logger.getLogger(GraalvmAgentApplication.class.getName()).info("WebSetup SPI providers: " + providerNames);
    }

    private static void webserver(String[] args) {
        WebServerBuilder webServerBuilder = Application.webServerBuilder(0, ZrLogUtil.getContextPath(args), null);
        Constants.zrLogConfig.getServerConfig().addCreateSuccessHandle(() -> {
            System.exit(0);
            return null;
        });
        webServerBuilder.start();
    }

}
