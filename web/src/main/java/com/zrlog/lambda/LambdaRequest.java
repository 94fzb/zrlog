package com.zrlog.lambda;

import com.hibegin.http.HttpMethod;
import com.hibegin.http.server.ApplicationContext;
import com.hibegin.http.server.config.RequestConfig;
import com.hibegin.http.server.impl.SimpleHttpRequest;
import com.zrlog.lambda.rest.LambdaApiGatewayRequest;

import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.Objects;

public class LambdaRequest extends SimpleHttpRequest {

    private final LambdaApiGatewayRequest lambdaApiGatewayRequest;


    protected LambdaRequest(ApplicationContext applicationContext, RequestConfig requestConfig, LambdaApiGatewayRequest lambdaApiGatewayRequest) {
        super(null, applicationContext, requestConfig);
        this.lambdaApiGatewayRequest = lambdaApiGatewayRequest;
        this.queryStr = lambdaApiGatewayRequest.getRawQueryString();
        this.method = HttpMethod.valueOf(lambdaApiGatewayRequest.getRequestContext().getHttp().getMethod());
        this.header = lambdaApiGatewayRequest.getHeaders();
        this.getHeaderMap().put("Host", lambdaApiGatewayRequest.getRequestContext().getDomainName());
        this.uri = lambdaApiGatewayRequest.getRequestContext().getHttp().getPath();
        if (Objects.nonNull(lambdaApiGatewayRequest.getBody()) && !lambdaApiGatewayRequest.getBody().isEmpty()) {
            if (Objects.equals(getLambdaApiGatewayRequest().getBase64Encoded(), true)) {
                this.inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(lambdaApiGatewayRequest.getBody()));
            } else {
                this.inputStream = new ByteArrayInputStream(lambdaApiGatewayRequest.getBody().getBytes());
            }
        }
    }

    public LambdaApiGatewayRequest getLambdaApiGatewayRequest() {
        return lambdaApiGatewayRequest;
    }
}
