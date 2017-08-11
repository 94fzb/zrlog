package com.fzb.blog.util.version;

import com.fzb.blog.util.ThumbnailUtil;
import com.fzb.blog.web.controller.admin.api.UploadController;
import com.fzb.blog.web.token.AdminToken;
import com.fzb.blog.web.token.AdminTokenThreadLocal;
import com.fzb.common.util.IOUtil;
import com.fzb.common.util.http.HttpUtil;
import com.fzb.common.util.http.handle.HttpFileHandle;
import com.jfinal.core.JFinal;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class V5UpgradeVersionHandler implements UpgradeVersionHandler {

    private static final Logger LOGGER = Logger.getLogger(V5UpgradeVersionHandler.class);

    @Override
    public void doUpgrade(Connection connection) throws SQLException, InterruptedException {
        Thread.sleep(10000L);
        PreparedStatement ps = connection.prepareStatement("select logid,content from log");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int id = rs.getInt("logid");
            String imgUrl = getFirstImgUrl(rs.getString("content"));
            PreparedStatement ups = connection.prepareStatement("update log set thumbnail = ? where logid = ?");
            ups.setString(1, imgUrl);
            ups.setInt(2, id);
            ups.execute();
        }
    }

    private static String getFirstImgUrl(String htmlContent) {
        UploadController uploadController = new UploadController();
        Elements elements = Jsoup.parse(htmlContent).select("img");
        if (!elements.isEmpty()) {
            String url = elements.first().attr("src");
            try {
                AdminToken adminToken = new AdminToken();
                adminToken.setUserId(1);
                AdminTokenThreadLocal.setAdminToken(adminToken);
                new File(JFinal.me().getConstants().getBaseUploadPath()).mkdirs();
                HttpFileHandle fileHandler = new HttpFileHandle(JFinal.me().getConstants().getBaseUploadPath());
                String path = new URL(url).getPath();
                path = path.substring(0, path.indexOf(".")) + "_thumbnail" + path.substring(path.indexOf("."));
                HttpUtil.getInstance().sendGetRequest(url, new HashMap<String, String[]>(), fileHandler, new HashMap<String, String>());
                byte[] bytes = IOUtil.getByteByInputStream(new FileInputStream(fileHandler.getT().getPath()));
                File thumbnailFile = new File(fileHandler.getT().getPath() + ".tmp");
                if (bytes.length > 0) {
                    IOUtil.writeBytesToFile(ThumbnailUtil.jpeg(bytes, 1f), thumbnailFile);
                    return uploadController.getCloudUrl(JFinal.me().getContextPath(), path, thumbnailFile.getPath()) + "?v=1";
                }
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
        return null;
    }
}
