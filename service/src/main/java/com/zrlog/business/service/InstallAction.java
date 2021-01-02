package com.zrlog.business.service;

public interface InstallAction {

    /**
     * 当安装流程正常执行完成时，调用了该方法，主要用于配置，启动JFinal插件功能，以及相应的Zrlog的插件服务。
     */
    void installFinish();
}
