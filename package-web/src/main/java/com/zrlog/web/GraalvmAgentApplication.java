package com.zrlog.web;

import com.google.gson.Gson;
import com.hibegin.common.util.IOUtil;
import com.hibegin.http.server.WebServerBuilder;
import com.hibegin.http.server.util.PathUtil;
import com.hibegin.lambda.rest.ApiGatewayHttp;
import com.hibegin.lambda.rest.ApiGatewayRequestContext;
import com.hibegin.lambda.rest.LambdaApiGatewayRequest;
import com.hibegin.lambda.rest.LambdaApiGatewayResponse;
import com.zrlog.common.ZrLogConfig;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.ZrLogUtil;

import java.util.*;

/**
 * 该类仅提供基础的 graalvm 运行依赖反射扫描等功能，不作为实际的启动入口
 */
public class GraalvmAgentApplication {

    static {
        System.getProperties().put("java.util.logging.SimpleFormatter.format", "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$s %5$s%6$s%n");
    }

    private static void lambdaJson() {
        new Gson().toJson(new LambdaApiGatewayRequest());
        new Gson().toJson(new LambdaApiGatewayResponse());
        new Gson().fromJson(IOUtil.getStringInputStream(GraalvmAgentApplication.class.getResourceAsStream("/lambda.json")), LambdaApiGatewayRequest.class);
        new Gson().toJson(new ApiGatewayHttp());
        new Gson().toJson(new ApiGatewayRequestContext());
    }

    public static void main(String[] args) throws Exception {
        ZrLogConfig.nativeImageAgent = true;
        BlogBuildInfoUtil.getBlogProp();
        PathUtil.setRootPath(System.getProperty("user.dir").replace("/package-web/target", ""));
        lambdaJson();
        //last
        webserver(args);
    }

    private static void webserver(String[] args) {
        WebServerBuilder webServerBuilder = Application.webServerBuilder(0, ZrLogUtil.getContextPath(args), null);
        webServerBuilder.addCreateSuccessHandle(() -> {
            System.exit(0);
            return null;
        });
        webServerBuilder.startWithThread();
    }


}
