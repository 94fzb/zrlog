package com.zrlog.test;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Random;

public class TestApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestApplication.class);

    private Tomcat tomcat;

    @Before
    public void before() throws LifecycleException, ServletException {
        System.setProperty("env", "junit-test");
        tomcat = new Tomcat();
        //1024~21023中随机未占用的端口（Linux低端口）
        int port = new Random().nextInt(20000) + 1024;
        while (!testPortCanBind(port)) {
            port = new Random().nextInt(20000) + 1024;
        }
        tomcat.setPort(port);
        tomcat.setBaseDir("target/classes");
        tomcat.addWebapp("", new File("src/main/webapp/").getAbsolutePath());
        tomcat.start();
    }

    @After
    public void after() throws LifecycleException {
        tomcat.stop();
    }

    public boolean testPortCanBind(int port) {
        try (ServerSocket ignored = new ServerSocket(port)) {
            LOGGER.info("Port {} can bind", port);
        } catch (IOException e) {
            LOGGER.warn("", e);
            return false;
        }
        return true;
    }
}
