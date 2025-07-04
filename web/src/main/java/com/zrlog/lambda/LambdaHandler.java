package com.zrlog.lambda;

import com.google.gson.Gson;
import com.hibegin.common.util.IOUtil;
import com.hibegin.http.server.ApplicationContext;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.handler.HttpRequestHandlerRunnable;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.common.ZrLogConfig;
import com.zrlog.lambda.rest.LambdaApiGatewayRequest;
import jdk.jpackage.internal.IOUtils;

import java.io.File;
import java.util.logging.Logger;

public class LambdaHandler {


    private final ApplicationContext applicationContext;
    private final ZrLogConfig serverConfig;

    public LambdaHandler(ZrLogConfig zrLogConfig) {
        this.serverConfig = zrLogConfig;
        applicationContext = new ApplicationContext(serverConfig.getServerConfig());
        applicationContext.init();
    }

    /**
     * @param lambdaApiGatewayRequest
     * @return
     */
    public LambdaResponse doHandle(LambdaApiGatewayRequest lambdaApiGatewayRequest) {
        HttpRequest request = new LambdaRequest(applicationContext, serverConfig.getRequestConfig(), lambdaApiGatewayRequest);
        LambdaResponse lambdaResponse = new LambdaResponse(request, serverConfig.getResponseConfig());
        new HttpRequestHandlerRunnable(request, lambdaResponse).run();
        return lambdaResponse;
    }
}
