package com.zrlog.web;

import com.zrlog.common.Constants;
import com.zrlog.common.type.RunMode;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import java.io.File;

/**
 * 支持将 SimpleWebServer 的包，构建为标准 web 也能运行的程序（war包）
 * 该文件不打包进入生产环境，仅开发调试用
 */
public class JakartaServletApplication {

    public static void main(String[] args) throws LifecycleException {
        // Declare an alternative location for your "WEB-INF/classes" dir
        // Servlet 3.0 annotation will work
        Constants.init();
        Constants.runMode = RunMode.DEV;
        File additionWebInfClasses = new File("target/classes");
        String webappDirLocation = "src/main/webapp/";
        Tomcat tomcat = new Tomcat();
        tomcat.getConnector();

        String webPort = System.getenv("PORT");
        if (webPort == null || webPort.isEmpty()) {
            webPort = "8080";
        }

        tomcat.setPort(Integer.parseInt(webPort));
        tomcat.setBaseDir(additionWebInfClasses.toString());
        //idea的路径eclipse启动的路径有区别
        if (!new File("").getAbsolutePath().endsWith(File.separator + "package-war-web")) {
            webappDirLocation = "package-war-web/" + webappDirLocation;
        }
        String contextPath = System.getenv("contextPath");
        if (contextPath == null || contextPath.trim().isEmpty()) {
            contextPath = "";
        }
        tomcat.setAddDefaultWebXmlToWebapp(true);
        tomcat.addWebapp(contextPath, new File(webappDirLocation).getAbsolutePath());
        tomcat.start();
        tomcat.getServer().await();
    }
}
