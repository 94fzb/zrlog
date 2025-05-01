package com.zrlog.web;

import com.zrlog.plugin.Plugins;

/**
 * 模块配置
 * 及需要在对应 Interceptor 中自行通过路由进行拦截，和设置路由
 */
public interface WebSetup {

    void setup();

    default Plugins getPlugins() {
        return new Plugins();
    }
}
