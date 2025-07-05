package com.hibegin.lambda;

import com.hibegin.common.util.ObjectUtil;
import com.hibegin.http.HttpMethod;
import com.hibegin.http.server.ApplicationContext;
import com.hibegin.http.server.config.RequestConfig;
import com.hibegin.http.server.config.ServerConfig;
import com.hibegin.http.server.impl.SimpleHttpRequest;
import com.hibegin.http.server.util.HttpQueryStringUtils;
import com.hibegin.lambda.rest.LambdaApiGatewayRequest;

import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.Objects;

public class LambdaHttpRequestWrapper extends SimpleHttpRequest {

    private final LambdaApiGatewayRequest lambdaApiGatewayRequest;


    protected LambdaHttpRequestWrapper(ApplicationContext applicationContext, RequestConfig requestConfig, LambdaApiGatewayRequest lambdaApiGatewayRequest) {
        super(null, applicationContext, requestConfig);
        this.lambdaApiGatewayRequest = lambdaApiGatewayRequest;
        this.queryStr = ObjectUtil.requireNonNullElse(lambdaApiGatewayRequest.getRawQueryString(), "");
        this.method = HttpMethod.valueOf(lambdaApiGatewayRequest.getRequestContext().getHttp().getMethod());
        this.header = lambdaApiGatewayRequest.getHeaders();
        this.paramMap = HttpQueryStringUtils.parseUrlEncodedStrToMap(this.queryStr);
        this.getHeaderMap().put("Host", lambdaApiGatewayRequest.getRequestContext().getDomainName());
        this.uri = lambdaApiGatewayRequest.getRequestContext().getHttp().getPath();
        if (Objects.nonNull(lambdaApiGatewayRequest.getBody()) && !lambdaApiGatewayRequest.getBody().isEmpty()) {
            if (getLambdaApiGatewayRequest().isBase64Encoded()) {
                this.inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(lambdaApiGatewayRequest.getBody()));
            } else {
                this.inputStream = new ByteArrayInputStream(lambdaApiGatewayRequest.getBody().getBytes());
            }
        }
        //
        ServerConfig serverConfig = super.getServerConfig();
        serverConfig.setApplicationName("Lambda Function");
        serverConfig.setApplicationVersion(LambdaEventIterator.VERSION);
    }

    public LambdaApiGatewayRequest getLambdaApiGatewayRequest() {
        return lambdaApiGatewayRequest;
    }
}
