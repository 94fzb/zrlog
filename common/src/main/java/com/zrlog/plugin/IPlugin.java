package com.zrlog.plugin;

public interface IPlugin {
    boolean start();

    boolean isStarted();

    boolean stop();
}
