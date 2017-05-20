package com.fzb.blog.web;

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

        //StandardContext ctx = (StandardContext) tomcat.addWebapp("/", );
        //System.out.println("configuring app with basedir: " + new File("/" + webappDirLocation).getAbsolutePath());

        // Declare an alternative location for your "WEB-INF/classes" dir
        // Servlet 3.0 annotation will work
        File additionWebInfClasses = new File("target/classes");
        //resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes",additionWebInfClasses.getAbsolutePath(), "/"));
        //ctx.setResources(resources);

        tomcat.setBaseDir(additionWebInfClasses.toString());
        tomcat.addWebapp("", new File(webappDirLocation).getAbsolutePath());
        tomcat.start();
        tomcat.getServer().await();
    }
}
