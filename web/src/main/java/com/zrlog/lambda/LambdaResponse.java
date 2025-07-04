package com.zrlog.lambda;

import com.google.gson.Gson;
import com.hibegin.common.util.BytesUtil;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.config.ResponseConfig;
import com.hibegin.http.server.impl.SimpleHttpResponse;
import com.zrlog.lambda.rest.LambdaApiGatewayResponse;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class LambdaResponse extends SimpleHttpResponse {
    public LambdaResponse(HttpRequest request, ResponseConfig responseConfig) {
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

    public String getOutput() {
        byte[] bytes = BytesUtil.mergeBytes(bodyList.toArray(new byte[bodyList.size()][]));
        LambdaApiGatewayResponse lambdaApiGatewayResponse = new LambdaApiGatewayResponse();
        lambdaApiGatewayResponse.setBody(new String(bytes));
        lambdaApiGatewayResponse.setStatusCode(statusCode);
        lambdaApiGatewayResponse.setHeaders(getHeader());
        return new Gson().toJson(lambdaApiGatewayResponse);
    }
}
