package com.zrlog.web.dev;

import com.zrlog.common.Constants;
import com.zrlog.util.ArgsParser;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import java.io.File;

/**
 * 以标准 web 能运行的程序（Servlet，仅 war 包）
 * 该文件不打包进入生产环境，仅开发调试用
 */
public class JakartaServletDevApplication {

    static {
        System.setProperty("sws.run.mode", "dev");
    }

    public static void main(String[] args) throws LifecycleException {
        Constants.init();
        File additionWebInfClasses = new File("target/classes");
        String webappDirLocation = "src/main/webapp/";
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(ArgsParser.getPort(args));
        tomcat.getConnector();
        tomcat.setBaseDir(additionWebInfClasses.toString());
        if (!new File("").getAbsolutePath().endsWith(File.separator + "package")) {
            webappDirLocation = "package/" + webappDirLocation;
        }
        tomcat.setAddDefaultWebXmlToWebapp(true);
        String contextPath = ArgsParser.getContextPath(args);
        String webappPath = new File(webappDirLocation).getAbsolutePath();
        tomcat.addWebapp(contextPath, webappPath);
        tomcat.start();
        tomcat.getServer().await();
    }
}
