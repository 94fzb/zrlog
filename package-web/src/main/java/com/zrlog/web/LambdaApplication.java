package com.zrlog.web;

import com.zrlog.business.service.NativeImageUpdater;
import com.zrlog.common.Constants;
import com.zrlog.lambda.LambdaHandler;
import com.zrlog.util.ZrLogUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class LambdaApplication {

    public static void initLambdaEnv() {
        if (!Constants.runMode.isLambda()) {
            return;
        }
        System.getProperties().put("sws.log.path", "/tmp/log");
        System.getProperties().put("sws.temp.path", "/tmp/temp");
        System.getProperties().put("sws.cache.path", "/tmp/cache");
        System.getProperties().put("sws.static.path", "/tmp/static");
        System.getProperties().put("sws.root.path", System.getProperty("user.dir"));
    }

    public static void doHandle(String[] args, int port, String execFile) throws Exception {
        initLambdaEnv();
        Application.webServerBuilder(port, ZrLogUtil.getContextPath(args), new NativeImageUpdater(args, new File(execFile)));
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String inputJson = reader.lines().collect(Collectors.joining("\n")).trim();
        String out = new LambdaHandler(Constants.zrLogConfig).doHandle(inputJson);
        System.out.println(out);
    }
}
