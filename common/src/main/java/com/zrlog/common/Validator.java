package com.zrlog.common;

public interface Validator {

    void doValid();

    default void doClean() {
    }
}
