package com.zrlog.lambda;

import com.hibegin.http.server.ApplicationContext;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.handler.HttpRequestHandlerRunnable;
import com.zrlog.common.ZrLogConfig;
import com.zrlog.lambda.rest.LambdaApiGatewayRequest;
import com.zrlog.lambda.rest.LambdaApiGatewayResponse;

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
    public LambdaApiGatewayResponse doHandle(LambdaApiGatewayRequest lambdaApiGatewayRequest) {
        HttpRequest request = new LambdaHttpRequestWrapper(applicationContext, serverConfig.getRequestConfig(), lambdaApiGatewayRequest);
        LambdaHttpResponseWrapper lambdaHttpResponseWrapper = new LambdaHttpResponseWrapper(request, serverConfig.getResponseConfig());
        new HttpRequestHandlerRunnable(request, lambdaHttpResponseWrapper).run();
        return lambdaHttpResponseWrapper.getOutput();
    }
}
