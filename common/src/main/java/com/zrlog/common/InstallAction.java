package com.zrlog.common;

public interface InstallAction {

    /**
     * 当安装流程正常执行完成时，调用了该方法，主要用于配置，启动插件功能，以及相应的ZrLog的插件服务。
     */
    void installFinish();
}
