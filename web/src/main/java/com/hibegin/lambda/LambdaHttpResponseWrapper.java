package com.hibegin.lambda;

import com.hibegin.common.util.BytesUtil;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.config.ResponseConfig;
import com.hibegin.http.server.impl.SimpleHttpResponse;
import com.hibegin.lambda.rest.LambdaApiGatewayResponse;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

public class LambdaHttpResponseWrapper extends SimpleHttpResponse {
    public LambdaHttpResponseWrapper(HttpRequest request, ResponseConfig responseConfig) {
        super(request, responseConfig);
    }

    private final List<byte[]> bodyList = new ArrayList<>();
    private int statusCode;

    @Override
    protected boolean needChunked(InputStream inputStream, long bodyLength) {
        return false;
    }

    @Override
    protected void send(byte[] bytes, boolean body, boolean close) {
        if (body) {
            bodyList.add(bytes);
        }
    }

    @Override
    protected byte[] wrapperBaseResponseHeader(int statusCode) {
        this.statusCode = statusCode;
        return super.wrapperBaseResponseHeader(statusCode);
    }

    public boolean isRequiredBase64Encode() {
        String contentType = getHeader().get("Content-Type");
        if (Objects.isNull(contentType)) {
            return true;
        }
        if (contentType.startsWith("application/json")) {
            return false;
        }
        if (contentType.startsWith("text/")) {
            return false;
        }
        if (contentType.startsWith("application/javascript")) {
            return false;
        }
        return true;
    }

    public LambdaApiGatewayResponse getOutput() {
        byte[] bytes = BytesUtil.mergeBytes(bodyList.toArray(new byte[bodyList.size()][]));
        LambdaApiGatewayResponse lambdaApiGatewayResponse = new LambdaApiGatewayResponse();
        if (isRequiredBase64Encode()) {
            lambdaApiGatewayResponse.setBody(Base64.getEncoder().encodeToString(bytes));
            lambdaApiGatewayResponse.setBase64Encoded(true);
        } else {
            lambdaApiGatewayResponse.setBody(new String(bytes));
        }
        lambdaApiGatewayResponse.setStatusCode(statusCode);
        lambdaApiGatewayResponse.setHeaders(getHeader());
        return lambdaApiGatewayResponse;
    }
}
