package com.zrlog.lambda;

import com.hibegin.http.server.ApplicationContext;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.handler.HttpRequestHandlerRunnable;
import com.zrlog.common.ZrLogConfig;
import com.zrlog.lambda.rest.LambdaApiGatewayRequest;

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
