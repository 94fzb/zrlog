package com.zrlog.business.version;

import com.hibegin.common.dao.DAO;


/**
 * 用于在版本升级过程中，无法单纯通过SQL更新的问题，使用代码扩展性更强
 */
public interface UpgradeVersionHandler {

    void doUpgrade(DAO dao) throws Exception;
}
