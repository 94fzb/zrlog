package com.zrlog.web;

import com.hibegin.http.server.WebServerBuilder;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.util.NativeImageUpdater;
import com.zrlog.util.ZrLogUtil;

/**
 * graalvm 实际的启动入口，开发阶段不使用这个类进行启动
 */
public class GraalvmNativeImageApplication {

    public static void main(String[] args) {
        String runtimeRoot = System.getProperty("user.dir");
        PathUtil.setRootPath(runtimeRoot);
        int port = ZrLogUtil.getPort(args);
        WebServerBuilder webServerBuilder = Application.webServerBuilder(args, port, new NativeImageUpdater(args, runtimeRoot), false);
        webServerBuilder.start();
    }
}
