package com.hibegin.lambda.rest;

public class ApiGatewayRequestContext {

    private ApiGatewayHttp http;
    private String domainName;
    private String requestId;

    public ApiGatewayHttp getHttp() {
        return http;
    }

    public void setHttp(ApiGatewayHttp http) {
        this.http = http;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
