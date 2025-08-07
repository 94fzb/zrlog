package com.zrlog.web.dev;

import com.hibegin.http.server.util.PathUtil;
import com.zrlog.web.Application;

import java.io.File;
import java.net.URISyntaxException;

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
