package com.hibegin.lambda.rest;

import com.google.gson.annotations.SerializedName;

import java.util.Map;
import java.util.Objects;

public class LambdaApiGatewayRequest {

    private String rawPath;

    private ApiGatewayRequestContext requestContext;

    private String rawQueryString;

    @SerializedName("isBase64Encoded")
    private Boolean isBase64Encoded;

    public String getRawQueryString() {
        return rawQueryString;
    }

    public void setRawQueryString(String rawQueryString) {
        this.rawQueryString = rawQueryString;
    }

    private Map<String, String> headers;

    private String body;

    public ApiGatewayRequestContext getRequestContext() {
        return requestContext;
    }

    public void setRequestContext(ApiGatewayRequestContext requestContext) {
        this.requestContext = requestContext;
    }

    public String getRawPath() {
        return rawPath;
    }

    public void setRawPath(String rawPath) {
        this.rawPath = rawPath;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isBase64Encoded() {
        return Objects.equals(isBase64Encoded, true);
    }

    public void setBase64Encoded(Boolean base64Encoded) {
        this.isBase64Encoded = Objects.equals(base64Encoded, true);
    }
}
