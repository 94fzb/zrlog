package com.zrlog.plugin;

public interface IPlugin {
    boolean start();

    default boolean autoStart() {
        return true;
    }

    boolean isStarted();

    boolean stop();
}
