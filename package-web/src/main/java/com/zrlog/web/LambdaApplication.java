package com.zrlog.web;

import com.google.gson.Gson;
import com.hibegin.common.util.EnvKit;
import com.zrlog.business.service.NativeImageUpdater;
import com.zrlog.common.Constants;
import com.zrlog.common.type.RunMode;
import com.zrlog.lambda.LambdaHandler;
import com.zrlog.lambda.rest.LambdaApiGatewayRequest;
import com.zrlog.lambda.rest.LambdaApiGatewayResponse;
import com.zrlog.util.ZrLogUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.AbstractMap;
import java.util.Map;
import java.util.logging.Logger;

public class LambdaApplication {

    private static final Logger LOGGER = Logger.getLogger(LambdaApplication.class.getName());

    private static String getBaseUrl() {
        return "http://" + System.getenv("AWS_LAMBDA_RUNTIME_API") + "/2018-06-01/runtime/invocation";
    }

    private static final HttpClient httpClient = HttpClient.newBuilder().build();

    static {
        initLambdaEnv();
    }

    private static void initLambdaEnv() {
        if (!RunMode.isLambdaEnv()) {
            return;
        }
        System.getProperties().put("sws.log.path", "/tmp/log");
        System.getProperties().put("sws.temp.path", "/tmp/temp");
        System.getProperties().put("sws.cache.path", "/tmp/cache");
        System.getProperties().put("sws.static.path", "/tmp/static");
        System.getProperties().put("sws.root.path", System.getProperty("user.dir"));
    }

    private static String getDevInput() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder inputBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            inputBuilder.append(line).append("\n");
        }
        return inputBuilder.toString().trim();
    }

    /**
     * http://${AWS_LAMBDA_RUNTIME_API}/2018-06-01/runtime/invocation/next
     * @return
     * @throws IOException
     */
    private static Map.Entry<String, LambdaApiGatewayRequest> getRequestInfo() throws IOException, InterruptedException {
        if (EnvKit.isDevMode()) {
            LambdaApiGatewayRequest apiGatewayRequest = new Gson().fromJson(getDevInput(), LambdaApiGatewayRequest.class);
            return new AbstractMap.SimpleEntry<>(apiGatewayRequest.getRequestContext().getRequestId(), apiGatewayRequest);
        }
        HttpResponse<String> send = httpClient.send(HttpRequest.newBuilder(URI.create(getBaseUrl() + "/next")).build(), HttpResponse.BodyHandlers.ofString());
        LambdaApiGatewayRequest apiGatewayRequest = new Gson().fromJson(send.body(), LambdaApiGatewayRequest.class);
        LOGGER.info("lambda request = " + new Gson().toJson(apiGatewayRequest));
        return new AbstractMap.SimpleEntry<>(send.headers().firstValue("Lambda-Runtime-Aws-Request-Id").orElse(""), apiGatewayRequest);
    }

    private static void report(String output, String requestId) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(getBaseUrl() + "/" + requestId + "/response"));
        builder.POST(HttpRequest.BodyPublishers.ofString(output));
        HttpResponse<String> send = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        if (send.statusCode() == 200) {
            LOGGER.info("lambda main server response = " + new Gson().toJson(send.body()));
        }
    }

    public static void doHandle(String[] args, int port, String execFile) throws Exception {
        Application.webServerBuilder(port, ZrLogUtil.getContextPath(args), new NativeImageUpdater(args, new File(execFile)));
        //处理请求
        while (true) {
            Map.Entry<String, LambdaApiGatewayRequest> requestInfo = getRequestInfo();
            LambdaApiGatewayResponse apiGatewayResponse = new LambdaHandler(Constants.zrLogConfig).doHandle(getRequestInfo().getValue());
            String output = new Gson().toJson(apiGatewayResponse);
            LOGGER.info("lambda response = " + output);
            if (EnvKit.isDevMode()) {
                return;
            }
            report(output, requestInfo.getKey());
        }
    }
}
