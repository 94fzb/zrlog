package com.zrlog.web;

import com.zrlog.business.service.NativeImageUpdater;
import com.zrlog.common.Constants;
import com.zrlog.common.type.RunMode;
import com.zrlog.lambda.LambdaEventIterator;
import com.zrlog.lambda.LambdaHandler;
import com.zrlog.lambda.rest.LambdaApiGatewayRequest;
import com.zrlog.lambda.rest.LambdaApiGatewayResponse;
import com.zrlog.util.ZrLogUtil;

import java.io.File;
import java.util.Map;

public class LambdaApplication {

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

    public static void doHandle(String[] args, int port, String execFile) throws Exception {
        Application.webServerBuilder(port, ZrLogUtil.getContextPath(args), new NativeImageUpdater(args, new File(execFile)));
        LambdaEventIterator lambdaEventIterator = new LambdaEventIterator();
        LambdaHandler lambdaHandler = new LambdaHandler(Constants.zrLogConfig);
        //处理请求
        while (lambdaEventIterator.hasNext()) {
            Map.Entry<String, LambdaApiGatewayRequest> requestInfo = lambdaEventIterator.next();
            LambdaApiGatewayResponse apiGatewayResponse = lambdaHandler.doHandle(requestInfo.getValue());
            lambdaEventIterator.report(apiGatewayResponse, requestInfo.getKey());
        }
    }
}
