package com.fzb.blog.util;

import com.fzb.blog.model.User;
import com.jfinal.core.JFinal;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ZrlogUtil {

    private static final Logger LOGGER = Logger.getLogger(ZrlogUtil.class);

    private ZrlogUtil() {

    }

    public static String getPluginServer() {
        return JFinal.me().getServletContext().getAttribute("pluginServer").toString();
    }

    public static Map<String, String> genHeaderMapByRequest(HttpServletRequest request) {
        Map<String, String> map = new HashMap<String, String>();
        User user = (User) request.getSession().getAttribute("user");
        if (user != null) {
            map.put("LoginUserName", user.get("userName").toString());
            map.put("LoginUserId", user.get("userId").toString());
        }
        map.put("IsLogin", (user != null) + "");
        map.put("Blog-Version", ((Map) JFinal.me().getServletContext().getAttribute("zrlog")).get("version").toString());
        map.put("Full-Url", request.getRequestURL().toString());
        map.put("Cookie", request.getHeader("Cookie"));
        map.put("AccessUrl", request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath());
        if (request.getHeader("Content-Type") != null) {
            map.put("Content-Type", request.getHeader("Content-Type"));
        }
        return map;
    }

    public static String getDatabaseServerVersion(String jdbcUrl, String userName, String password, String deriveClass) {
        Connection connect = null;
        try {
            Class.forName(deriveClass);
            connect = DriverManager.getConnection(jdbcUrl, userName, password);
            String queryVersionSQL = "select version()";
            PreparedStatement ps = connect.prepareStatement(queryVersionSQL);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                return  resultSet.getString(1);
            }
        } catch (Exception e) {
            LOGGER.error("Not can same deriveClass " + deriveClass, e);
        } finally {
            if (connect != null) {
                try {
                    connect.close();
                } catch (SQLException e) {
                    LOGGER.error(e);
                }
            }
        }
        return "Unknown";
    }
}
