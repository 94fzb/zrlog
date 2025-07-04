package com.zrlog.lambda.rest;

import java.util.Map;

public class LambdaApiGatewayResponse {

    private String body;

    private Integer statusCode;

    private Map<String, String> headers;

    private Boolean base64Encoded;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Boolean getBase64Encoded() {
        return base64Encoded;
    }

    public void setBase64Encoded(Boolean base64Encoded) {
        this.base64Encoded = base64Encoded;
    }
}
