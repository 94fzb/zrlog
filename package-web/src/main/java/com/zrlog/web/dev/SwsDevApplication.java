package com.zrlog.web.dev;

import com.hibegin.http.server.util.PathUtil;
import com.zrlog.web.Application;

import java.io.File;
import java.net.URISyntaxException;

/**
 * 以 SimpleWebServer 容器进行运行，除 war 包以外都用这个入口启动调试
 * 该文件不打包进入生产环境，仅开发调试用
 */
public class SwsDevApplication {


    static {
        System.setProperty("sws.run.mode", "dev");
    }

    private static void initDevEnv() throws URISyntaxException {
        String programDir = new File(SwsDevApplication.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
        //对应的开发模式
        if (programDir.contains("package-web/target/class") || programDir.contains("package-web\\target\\class")) {
            programDir = new File(programDir).getParentFile().getParentFile().getParent();
        }
        PathUtil.setRootPath(programDir);
    }

    public static void main(String[] args) throws Exception {
        initDevEnv();
        Application.start(args);
    }
}
