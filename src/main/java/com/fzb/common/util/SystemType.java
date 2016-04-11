package com.fzb.common.util;

public enum SystemType {

    LINUX(0), WINDOWS(1);
    private int type;

    SystemType(int type) {
        this.setType(type);
    }

    @Override
    public String toString() {
        return String.valueOf(this.getType());

    }

    public int getType() {
        return type;
    }

    public SystemType setType(int type) {
        this.type = type;
        return this;
    }
}
