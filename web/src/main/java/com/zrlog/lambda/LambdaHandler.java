package com.zrlog.lambda;

import com.google.gson.Gson;
import com.hibegin.http.server.ApplicationContext;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.handler.HttpRequestHandlerRunnable;
import com.zrlog.common.ZrLogConfig;
import com.zrlog.lambda.rest.LambdaApiGatewayRequest;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class LambdaHandler {


    private final ApplicationContext applicationContext;
    private final ZrLogConfig serverConfig;

    public LambdaHandler(ZrLogConfig zrLogConfig) {
        this.serverConfig = zrLogConfig;
        applicationContext = new ApplicationContext(serverConfig.getServerConfig());
        applicationContext.init();
    }

    public static String toQueryString(Map<String, String> queryParams) {
        if (queryParams == null || queryParams.isEmpty()) return "";

        StringBuilder queryString = new StringBuilder();
        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            String key = URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8);
            String value = URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8);
            if (queryString.length() > 0) {
                queryString.append("&");
            }
            queryString.append(key).append("=").append(value);
        }
        return queryString.toString();
    }

    /**
     * {
     * "resource": "/myresource",
     * "path": "/myresource",
     * "httpMethod": "GET",
     * "headers": { "Content-Type": "application/json" },
     * "queryStringParameters": { "name": "Alice" },
     * "body": null,
     * "isBase64Encoded": false
     * }
     *
     * @param input
     * @return
     */
    public String doHandle(String input) {
        LambdaApiGatewayRequest lambdaApiGatewayRequest = new Gson().fromJson(input, LambdaApiGatewayRequest.class);
        HttpRequest request = new LambdaRequest(applicationContext, serverConfig.getRequestConfig(), lambdaApiGatewayRequest);
        LambdaResponse lambdaResponse = new LambdaResponse(request, serverConfig.getResponseConfig());
        new HttpRequestHandlerRunnable(request, lambdaResponse).run();
        return lambdaResponse.getOutput();
    }
}
