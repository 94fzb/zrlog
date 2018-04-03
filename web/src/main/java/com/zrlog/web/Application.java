package com.zrlog.web;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;

public class Application {

    public static void main(String[] args) throws ServletException, LifecycleException, IOException {
        String webappDirLocation = "src/main/webapp/";
        Tomcat tomcat = new Tomcat();

        String webPort = System.getenv("PORT");
        if (webPort == null || webPort.isEmpty()) {
            webPort = "8080";
        }

        tomcat.setPort(Integer.valueOf(webPort));

        // Declare an alternative location for your "WEB-INF/classes" dir
        // Servlet 3.0 annotation will work
        File additionWebInfClasses = new File("target/classes");
        //resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes",additionWebInfClasses.getAbsolutePath(), "/"));
        //ctx.setResources(resources);

        tomcat.setBaseDir(additionWebInfClasses.toString());
        //idea的路径eclipse启动的路径有区别
        if (!new File("").getAbsolutePath().endsWith(File.separator + "web")) {
            webappDirLocation = "web/" + webappDirLocation;
        }
        tomcat.addWebapp("", new File(webappDirLocation).getAbsolutePath());
        tomcat.start();
        tomcat.getServer().await();
    }
}
