package com.zrlog.business.version;

import com.hibegin.common.dao.DAO;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class V5UpgradeVersionHandler implements UpgradeVersionHandler {

    @Override
    public void doUpgrade(DAO dao) throws SQLException, InterruptedException {
        Thread.sleep(10000L);
        List<Map<String, Object>> maps = dao.queryListWithParams("select logid,content from log");
        for (Map<String, Object> map : maps) {
            Number id = (Number) map.get("logid");
            dao.execute("update log set thumbnail = ? where logid = ?", "", id.longValue());
        }
    }
}
