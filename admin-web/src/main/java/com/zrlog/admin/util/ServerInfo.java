package com.zrlog.admin.util;

public class ServerInfo {

    private final String name;
    private final String value;
    private final String key;

    public ServerInfo(String name, String value, String key) {
        this.name = name;
        this.value = value;
        this.key = key;
    }


    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }
}