package com.zrlog.web;

import com.zrlog.util.ArgsParser;

class ApplicationStartupOptions {

    private final int port;
    private final String contextPath;

    private ApplicationStartupOptions(int port, String contextPath) {
        this.port = port;
        this.contextPath = contextPath;
    }

    static ApplicationStartupOptions parse(String[] args) {
        return new ApplicationStartupOptions(ArgsParser.getPort(args), ArgsParser.getContextPath(args));
    }

    int getPort() {
        return port;
    }

    String getContextPath() {
        return contextPath;
    }
}
