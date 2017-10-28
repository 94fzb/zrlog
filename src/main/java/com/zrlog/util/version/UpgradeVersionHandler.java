package com.zrlog.util.version;

import java.sql.Connection;

public interface UpgradeVersionHandler {

    void doUpgrade(Connection connection) throws Exception;
}
