package com.zrlog.common.vo;

public class DatabaseConnectPoolInfo {

    private final Integer connectActiveSize;
    private final Integer connectTotalSize;


    public DatabaseConnectPoolInfo(Integer connectActiveSize, Integer connectTotalSize) {
        this.connectActiveSize = connectActiveSize;
        this.connectTotalSize = connectTotalSize;
    }

    public Integer getConnectActiveSize() {
        return connectActiveSize;
    }

    public Integer getConnectTotalSize() {
        return connectTotalSize;
    }
}
