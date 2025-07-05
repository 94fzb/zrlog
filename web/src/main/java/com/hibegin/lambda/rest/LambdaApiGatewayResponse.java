package com.hibegin.lambda.rest;

import com.google.gson.annotations.SerializedName;

import java.util.Map;
import java.util.Objects;

public class LambdaApiGatewayResponse {

    private String body;

    private Integer statusCode;

    private Map<String, String> headers;

    @SerializedName("isBase64Encoded")
    private Boolean isBase64Encoded;

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

    public boolean isBase64Encoded() {
        return Objects.equals(isBase64Encoded, true);
    }

    public void setBase64Encoded(Boolean isBase64Encoded) {
        this.isBase64Encoded = Objects.equals(isBase64Encoded, true);
    }
}
