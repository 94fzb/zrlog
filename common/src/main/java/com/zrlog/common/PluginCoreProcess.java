package com.zrlog.common;

import java.io.File;

public interface PluginCoreProcess {

    /**
     * plugin-core 目前会监听2个TCP端口，一个用于和 ZrLog 通信，另一个用于和插件通信
     *
     * @param pluginCoreFile plugin-core.jar 存放的物理路径
     * @param dbProperties
     * @param pluginJvmArgs
     * @param runtimePath    plugin-core 的运行目录
     * @param runTimeVersion zrlog 目前的版本
     * @param token
     * @return
     */
    int pluginServerStart(final File pluginCoreFile, final String dbProperties, final String pluginJvmArgs,
                          final String runtimePath, final String runTimeVersion, String token);

    void stopPluginCore();
}
