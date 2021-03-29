package com.zrlog.blog.web.version;

import com.zrlog.common.vo.AdminTokenVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class V5UpgradeVersionHandler implements UpgradeVersionHandler {

    @Override
    public void doUpgrade(Connection connection) throws SQLException, InterruptedException {
        Thread.sleep(10000L);
        PreparedStatement userPs = connection.prepareStatement("select userid from user limit 1");
        int userId = 0;
        ResultSet userRs = userPs.executeQuery();
        if (userRs.next()) {
            userId = userRs.getInt("userid");
        }
        userPs.close();
        PreparedStatement ps = connection.prepareStatement("select logid,content from log");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int id = rs.getInt("logid");
            AdminTokenVO adminTokenVO = new AdminTokenVO();
            adminTokenVO.setUserId(userId);
            PreparedStatement ups = connection.prepareStatement("update log set thumbnail = ? where logid = ?");
            ups.setString(1, "");
            ups.setInt(2, id);
            ups.execute();
            ups.close();
        }
        ps.close();
    }
}
