package com.zrlog.web;

import com.zrlog.common.Constants;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import java.io.File;

public class Application {

    public static void main(String[] args) throws LifecycleException {
        String webappDirLocation;
        if (Constants.IN_JAR) {
            webappDirLocation = "webapp";
        } else {
            webappDirLocation = "src/main/webapp/";
        }

        Tomcat tomcat = new Tomcat();

        String webPort = System.getenv("PORT");
        if (webPort == null || webPort.isEmpty()) {
            webPort = "8080";
        }

        tomcat.setPort(Integer.parseInt(webPort));
        tomcat.getConnector();

        // Declare an alternative location for your "WEB-INF/classes" dir
        // Servlet 3.0 annotation will work
        File additionWebInfClasses;
        if (Constants.IN_JAR) {
            additionWebInfClasses = new File("");
        } else {
            additionWebInfClasses = new File("target/classes");
        }

        tomcat.setBaseDir(additionWebInfClasses.toString());
        //idea的路径eclipse启动的路径有区别
        if (!Constants.IN_JAR && !new File("").getAbsolutePath().endsWith(File.separator + "web")) {
            webappDirLocation = "web/" + webappDirLocation;
        }
        String contextPath = System.getenv("contextPath");
        if (contextPath == null || contextPath.trim().length() == 0) {
            contextPath = "";
        }
        tomcat.addWebapp(contextPath, new File(webappDirLocation).getAbsolutePath());
        tomcat.start();
        tomcat.getServer().await();
    }
}
