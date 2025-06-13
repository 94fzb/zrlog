package com.hibegin.http.server;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.Set;

public class PortDetector {

    public static Integer detectHttpPort() {
        try {
            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

            // 查询所有 Connector
            Set<ObjectName> names = mBeanServer.queryNames(new ObjectName("Tomcat:type=Connector,*"), null);

            for (ObjectName name : names) {
                String protocol = (String) mBeanServer.getAttribute(name, "protocol");
                if ("HTTP/1.1".equalsIgnoreCase(protocol) || protocol.contains("Http")) {
                    return (Integer) mBeanServer.getAttribute(name, "port");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}