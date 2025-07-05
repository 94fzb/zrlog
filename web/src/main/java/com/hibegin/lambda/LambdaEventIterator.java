package com.hibegin.lambda;

import com.google.gson.Gson;
import com.hibegin.common.util.EnvKit;
import com.hibegin.lambda.rest.LambdaApiGatewayRequest;
import com.hibegin.lambda.rest.LambdaApiGatewayResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

/**
 * <a href="https://docs.aws.amazon.com/lambda/latest/dg/runtimes-api.html">lambda runtime api</a>
 */
public class LambdaEventIterator implements Iterator<Map.Entry<String, LambdaApiGatewayRequest>> {

    private static final Logger LOGGER = Logger.getLogger(LambdaEventIterator.class.getName());

    private boolean hasNext = true;

    public static final String VERSION = "2018-06-01";

    private String getBaseUrl() {
        return "http://" + System.getenv("AWS_LAMBDA_RUNTIME_API") + "/" + VERSION + "/runtime/invocation";
    }

    private final HttpClient httpClient = HttpClient.newBuilder().build();

    private static String getDevInput() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder inputBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            inputBuilder.append(line).append("\n");
        }
        return inputBuilder.toString().trim();
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public Map.Entry<String, LambdaApiGatewayRequest> next() {
        try {
            Map.Entry<String, LambdaApiGatewayRequest> requestInfo = getRequestInfo();
            if (EnvKit.isDevMode()) {
                LOGGER.info("lambda request  " + requestInfo.getKey() + " : " + new Gson().toJson(requestInfo.getValue()));
            }
            return requestInfo;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * http://${AWS_LAMBDA_RUNTIME_API}/2018-06-01/runtime/invocation/next
     * @return
     * @throws IOException
     */
    private Map.Entry<String, LambdaApiGatewayRequest> getRequestInfo() throws IOException, InterruptedException {
        if (EnvKit.isDevMode()) {
            LambdaApiGatewayRequest apiGatewayRequest = new Gson().fromJson(getDevInput(), LambdaApiGatewayRequest.class);
            return new AbstractMap.SimpleEntry<>(apiGatewayRequest.getRequestContext().getRequestId(), apiGatewayRequest);
        }
        HttpResponse<String> send = httpClient.send(HttpRequest.newBuilder(URI.create(getBaseUrl() + "/next")).build(), HttpResponse.BodyHandlers.ofString());
        LambdaApiGatewayRequest apiGatewayRequest = new Gson().fromJson(send.body(), LambdaApiGatewayRequest.class);
        return new AbstractMap.SimpleEntry<>(send.headers().firstValue("Lambda-Runtime-Aws-Request-Id").orElse(""), apiGatewayRequest);
    }

    public void report(LambdaApiGatewayResponse response, String requestId) throws IOException, InterruptedException {
        String output = new Gson().toJson(response);
        if (EnvKit.isDevMode()) {
            LOGGER.info("lambda response " + requestId + " : " + output);
        }
        if (EnvKit.isDevMode()) {
            hasNext = false;
            return;
        }
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(getBaseUrl() + "/" + requestId + "/response"));
        builder.POST(HttpRequest.BodyPublishers.ofString(output));
        HttpResponse<String> send = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        if (EnvKit.isDevMode()) {
            LOGGER.info("lambda main server response = " + send.statusCode() + ":" + send.body());
        }
    }
}
