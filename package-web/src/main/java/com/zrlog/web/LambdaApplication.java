package com.zrlog.web;

import com.zrlog.business.service.NativeImageUpdater;
import com.zrlog.common.Constants;
import com.zrlog.common.type.RunMode;
import com.zrlog.lambda.LambdaHandler;
import com.zrlog.util.ZrLogUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class LambdaApplication {

    static {
        initLambdaEnv();
    }

    public static void initLambdaEnv() {
        if (!RunMode.isLambdaEnv()) {
            return;
        }
        System.getProperties().put("sws.log.path", "/tmp/log");
        System.getProperties().put("sws.temp.path", "/tmp/temp");
        System.getProperties().put("sws.cache.path", "/tmp/cache");
        System.getProperties().put("sws.static.path", "/tmp/static");
        System.getProperties().put("sws.root.path", System.getProperty("user.dir"));
    }

    public static String getInput() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder inputBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            inputBuilder.append(line).append("\n");
        }
        return inputBuilder.toString().trim();
    }

    public static void doHandle(String[] args, int port, String execFile) throws Exception {
        Application.webServerBuilder(port, ZrLogUtil.getContextPath(args), new NativeImageUpdater(args, new File(execFile)));
        String out = new LambdaHandler(Constants.zrLogConfig).doHandle(getInput());
        System.out.println(out);
    }
}
