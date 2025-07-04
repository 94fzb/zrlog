package com.zrlog.lambda.vo;

public class ApiGatewayRequestContext {

    private ApiGatewayHttp http;
    private String domainName;

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
}
