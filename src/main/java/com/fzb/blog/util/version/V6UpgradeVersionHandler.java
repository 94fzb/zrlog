package com.fzb.blog.util.version;

import com.fzb.blog.service.ArticleService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class V6UpgradeVersionHandler implements UpgradeVersionHandler {

    @Override
    public void doUpgrade(Connection connection) throws SQLException, InterruptedException {
        Thread.sleep(10000L);
        PreparedStatement ps = connection.prepareStatement("select content,logid from log");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int id = rs.getInt("logid");
            PreparedStatement ups = connection.prepareStatement("update log set search_content = ? where logid = ?");
            ups.setString(1, new ArticleService().getExtractSearchTxt(rs.getString("content")));
            ups.setInt(2, id);
            ups.execute();
        }
    }
}
