package com.zrlog.web.version;

import java.sql.Connection;

/**
 * 用于在版本升级过程中，无法单纯通过SQL更新的问题，使用代码扩展性更强
 */
public interface UpgradeVersionHandler {

    void doUpgrade(Connection connection) throws Exception;
}
