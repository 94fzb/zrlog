package com.fzb.blog.util;

import com.fzb.blog.model.User;
import com.fzb.blog.web.incp.AdminToken;
import com.fzb.blog.web.incp.AdminTokenThreadLocal;
import com.fzb.blog.web.util.WebTools;
import com.fzb.common.util.IOUtil;
import com.jfinal.core.JFinal;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.*;

/**
 * Zrlog特有的一些工具方法
 */
public class ZrlogUtil {

    private static final Logger LOGGER = Logger.getLogger(ZrlogUtil.class);

    private ZrlogUtil() {

    }

    public static String getPluginServer() {
        return JFinal.me().getServletContext().getAttribute("pluginServer").toString();
    }

    public static boolean isStaticBlogPlugin(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getHeader("User-Agent") != null && httpServletRequest.getHeader("User-Agent").startsWith("Static-Blog-Plugin");
    }

    public static Map<String, String> genHeaderMapByRequest(HttpServletRequest request) {
        Map<String, String> map = new HashMap<String, String>();
        AdminToken adminToken = AdminTokenThreadLocal.getUser();
        if (adminToken != null) {
            User user = User.dao.findById(adminToken.getUserId());
            map.put("LoginUserName", user.get("userName").toString());
            map.put("LoginUserId", adminToken.getUserId() + "");
        }
        map.put("IsLogin", (adminToken != null) + "");
        map.put("Blog-Version", ((Map) JFinal.me().getServletContext().getAttribute("zrlog")).get("version").toString());
        String fullUrl = getFullUrl(request);
        if (request.getQueryString() != null) {
            fullUrl = fullUrl + "?" + request.getQueryString();
        }
        map.put("Full-Url", fullUrl);
        map.put("Cookie", request.getHeader("Cookie"));
        map.put("AccessUrl", WebTools.getRealScheme(request) + "://" + request.getHeader("Host") + request.getContextPath());
        if (request.getHeader("Content-Type") != null) {
            map.put("Content-Type", request.getHeader("Content-Type"));
        }
        return map;
    }

    public static String getFullUrl(HttpServletRequest request) {
        return WebTools.getRealScheme(request) + "://" + request.getHeader("Host") + request.getRequestURI();
    }

    public static String getDatabaseServerVersion(String jdbcUrl, String userName, String password, String deriveClass) {
        Connection connect = null;
        try {
            connect = getConnection(jdbcUrl, userName, password, deriveClass);
            if (connect != null) {
                String queryVersionSQL = "select version()";
                PreparedStatement ps = connect.prepareStatement(queryVersionSQL);
                ResultSet resultSet = ps.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getString(1);
                }
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

    private static Connection getConnection(String jdbcUrl, String user, String password, String driverClass) {
        try {
            Class.forName(driverClass);
            return DriverManager.getConnection(jdbcUrl, user, password);
        } catch (ClassNotFoundException e) {
            LOGGER.error(e);
        } catch (SQLException e) {
            LOGGER.error(e);
        }
        return null;
    }

    public static Integer getSqlVersion(String basePath) {
        List<File> sqlFileList = getSqlFileList(basePath);
        if (!sqlFileList.isEmpty()) {
            return Integer.valueOf(sqlFileList.get(sqlFileList.size() - 1).getName().replace(".sql", ""));
        }
        return -1;
    }

    private static List<File> getSqlFileList(String basePath) {
        File file = new File(basePath);
        List<File> fileList = new ArrayList<File>();
        if (file.exists() && file.isDirectory()) {
            File[] fs = file.listFiles();
            if (fs != null && fs.length > 0) {
                fileList = Arrays.asList(fs);
                Comparator<File> comparator = new Comparator<File>() {
                    @Override
                    public int compare(File o1, File o2) {
                        Integer v1 = Integer.valueOf(o1.getName().replace(".sql", ""));
                        Integer v2 = Integer.valueOf(o2.getName().replace(".sql", ""));
                        if (v1 <= v2) {
                            return -1;
                        }
                        return 0;
                    }
                };
                Collections.sort(fileList, comparator);
            }
        }
        return fileList;
    }

    private static List<String> getExecSqlList(String sqlVersion, String basePath) {
        List<String> sqlList = new ArrayList<String>();
        Integer version = 0;
        try {
            version = Integer.valueOf(sqlVersion);
        } catch (Exception e) {
            LOGGER.error(e);
        }
        for (File f : getSqlFileList(basePath)) {
            try {
                Integer fileVersion = Integer.valueOf(f.getName().replace(".sql", ""));
                if (fileVersion > version) {
                    LOGGER.info("need update sql " + f);
                    sqlList.addAll(Arrays.asList(IOUtil.getStringInputStream(new FileInputStream(f)).split("\n")));
                }
            } catch (FileNotFoundException e) {
                LOGGER.error("", e);
            }
        }
        return sqlList;
    }

    public static Integer doUpgrade(String currentVersion, String basePath, String jdbcUrl, String user, String password, String driverClass) {
        List<String> sqlList = getExecSqlList(currentVersion, basePath);
        if (!sqlList.isEmpty()) {
            Connection connection = getConnection(jdbcUrl, user, password, driverClass);
            if (connection != null) {
                Statement statement = null;
                try {
                    for (String sql : sqlList) {
                        statement = connection.createStatement();
                        statement.execute(sql);
                    }
                } catch (SQLException e) {
                    LOGGER.error(e);
                } finally {
                    if (statement != null) {
                        try {
                            statement.close();
                        } catch (SQLException e) {
                            LOGGER.error(e);
                        }
                    }
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        LOGGER.error(e);
                    }
                }
            }
            return getSqlVersion(basePath);
        }
        return -1;
    }
}
