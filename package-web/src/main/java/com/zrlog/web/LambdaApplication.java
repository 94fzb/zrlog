package com.zrlog.web;

import com.hibegin.http.server.WebServerBuilder;
import com.zrlog.business.service.NativeImageUpdater;
import com.zrlog.common.Constants;
import com.zrlog.common.type.RunMode;
import com.hibegin.lambda.LambdaEventIterator;
import com.hibegin.lambda.LambdaHandler;
import com.hibegin.lambda.rest.LambdaApiGatewayRequest;
import com.hibegin.lambda.rest.LambdaApiGatewayResponse;
import com.zrlog.util.ZrLogUtil;

import java.io.File;
import java.util.Map;

public class LambdaApplication {

    static {
        initLambdaEnv();
    }

    public static void initLambdaEnv() {
        if (!RunMode.isLambdaMode()) {
            return;
        }
        System.getProperties().put("sws.log.path", "/tmp/log");
        System.getProperties().put("sws.temp.path", "/tmp/temp");
        System.getProperties().put("sws.cache.path", "/tmp/cache");
        System.getProperties().put("sws.static.path", "/tmp/static");
        System.getProperties().put("sws.conf.path", "/tmp/conf");
    }

    public static void doHandle(String[] args, int port) throws Exception {
        File file = new File(ZrLogUtil.getLambdaRoot() + "/zrlog");
        WebServerBuilder webServerBuilder = Application.webServerBuilder(port, ZrLogUtil.getContextPath(args), new NativeImageUpdater(args, file));
        webServerBuilder.startInBackground();
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
