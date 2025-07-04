package com.zrlog.lambda;

import com.google.gson.Gson;
import com.hibegin.http.HttpMethod;
import com.hibegin.http.server.ApplicationContext;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.handler.HttpRequestHandlerRunnable;
import com.hibegin.http.server.util.HttpRequestBuilder;
import com.zrlog.common.ZrLogConfig;
import com.zrlog.lambda.vo.LambdaApiGatewayRequest;

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
     * @throws Exception
     */
    public String doHandle(String input) throws Exception {
        LambdaApiGatewayRequest lambdaApiGatewayRequest = new Gson().fromJson(input, LambdaApiGatewayRequest.class);
        HttpRequest request = HttpRequestBuilder.buildRequest(HttpMethod.valueOf(lambdaApiGatewayRequest.getHttpMethod()),
                lambdaApiGatewayRequest.getPath() + "?" + toQueryString(lambdaApiGatewayRequest.getQueryStringParameters()),
                lambdaApiGatewayRequest.getHeaders().get("Host"),
                lambdaApiGatewayRequest.getHeaders().get("User-Agent"), serverConfig.getRequestConfig(), applicationContext);
        LambdaResponse lambdaResponse = new LambdaResponse(request, serverConfig.getResponseConfig());
        new HttpRequestHandlerRunnable(request, lambdaResponse).run();
        return lambdaResponse.getOutput();
    }
}
