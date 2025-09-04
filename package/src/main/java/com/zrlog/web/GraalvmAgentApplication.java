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
        PathUtil.setRootPath(System.getProperty("user.dir").replace("/package/target", ""));
        lambdaJson();
        //last
        webserver(args);
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
