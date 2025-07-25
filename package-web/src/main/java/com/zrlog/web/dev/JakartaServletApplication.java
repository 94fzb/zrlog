package com.zrlog.web.dev;

import com.zrlog.common.Constants;
import com.zrlog.util.ZrLogUtil;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import java.io.File;

/**
 * 支持将 SimpleWebServer 的包，构建为标准 web 也能运行的程序（war包）
 * 该文件不打包进入生产环境，仅开发调试用
 */
public class JakartaServletApplication {

    public static void main(String[] args) throws LifecycleException {
        System.setProperty("sws.run.mode", "dev");
        // Declare an alternative location for your "target/classes" dir
        Constants.init();
        File additionWebInfClasses = new File("target/classes");
        String webappDirLocation = "src/main/webapp/";
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(ZrLogUtil.getPort(args));
        //创建服务
        tomcat.getConnector();
        tomcat.setBaseDir(additionWebInfClasses.toString());
        //idea的路径eclipse启动的路径有区别
        if (!new File("").getAbsolutePath().endsWith(File.separator + "package-web")) {
            webappDirLocation = "package-web/" + webappDirLocation;
        }
        tomcat.setAddDefaultWebXmlToWebapp(true);
        tomcat.addWebapp(ZrLogUtil.getContextPath(args), new File(webappDirLocation).getAbsolutePath());
        tomcat.start();
        tomcat.getServer().await();
    }
}
