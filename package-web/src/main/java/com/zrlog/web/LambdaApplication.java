package com.zrlog.web;

import com.google.gson.Gson;
import com.hibegin.common.util.IOUtil;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.business.service.NativeImageUpdater;
import com.zrlog.common.Constants;
import com.zrlog.common.type.RunMode;
import com.zrlog.lambda.LambdaHandler;
import com.zrlog.lambda.LambdaResponse;
import com.zrlog.lambda.rest.LambdaApiGatewayRequest;
import com.zrlog.lambda.rest.LambdaApiGatewayResponse;
import com.zrlog.util.ZrLogUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

public class LambdaApplication {

    private static final Logger LOGGER = Logger.getLogger(LambdaApplication.class.getName());

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

    private static String getInput() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder inputBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            inputBuilder.append(line).append("\n");
        }
        return inputBuilder.toString().trim();
    }

    public static void doHandle(String[] args, int port, String execFile) throws Exception {
        LambdaApiGatewayRequest apiGatewayRequest = new Gson().fromJson(getInput(), LambdaApiGatewayRequest.class);
        LOGGER.info("lambda input = " + new Gson().toJson(apiGatewayRequest));
        Application.webServerBuilder(port, ZrLogUtil.getContextPath(args), new NativeImageUpdater(args, new File(execFile)));
        LambdaApiGatewayResponse apiGatewayResponse = new LambdaHandler(Constants.zrLogConfig).doHandle(apiGatewayRequest);
        String output = new Gson().toJson(apiGatewayResponse);
        IOUtil.writeStrToFile(output, new File(PathUtil.getTempPath() + "/" + apiGatewayRequest.getRequestContext().getRequestId() + ".json"));
        LOGGER.info("lambda output = " + output);
    }
}
