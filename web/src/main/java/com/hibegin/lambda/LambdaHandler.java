package com.hibegin.lambda;

import com.hibegin.http.server.ApplicationContext;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.config.AbstractServerConfig;
import com.hibegin.http.server.handler.HttpRequestHandlerRunnable;
import com.hibegin.lambda.rest.LambdaApiGatewayRequest;
import com.hibegin.lambda.rest.LambdaApiGatewayResponse;

public class LambdaHandler {


    private final ApplicationContext applicationContext;
    private final AbstractServerConfig serverConfig;

    public LambdaHandler(AbstractServerConfig serverConfig) {
        this.serverConfig = serverConfig;
        applicationContext = new ApplicationContext(this.serverConfig.getServerConfig());
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
