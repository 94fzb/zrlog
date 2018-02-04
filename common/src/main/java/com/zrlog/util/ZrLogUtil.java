package com.zrlog.util;

import com.google.gson.Gson;
import com.hibegin.common.util.IOUtil;
import com.jfinal.core.JFinal;
import com.zrlog.common.Constants;
import com.zrlog.web.util.WebTools;
import org.apache.log4j.Logger;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.*;

/**
 * Zrlog特有的一些工具方法
 */
public class ZrLogUtil {

    private static final Logger LOGGER = Logger.getLogger(ZrLogUtil.class);

    private ZrLogUtil() {

    }

    public static <T> T convertRequestBody(ServletRequest request, Class<T> clazz) {
        try {
            String jsonStr = IOUtil.getStringInputStream(request.getInputStream());
            return new Gson().fromJson(jsonStr, clazz);
        } catch (IOException e) {
            LOGGER.info("", e);
            throw new RuntimeException(e);
        }
    }

    public static String getPluginServer() {
        return JFinal.me().getServletContext().getAttribute("pluginServer").toString();
    }

    public static boolean isStaticBlogPlugin(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getHeader("User-Agent") != null && httpServletRequest.getHeader("User-Agent").startsWith("Static-Blog-Plugin");
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
                    String result = resultSet.getString(1);
                    ps.close();
                    return result;
                }
                ps.close();
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

    public static String getCurrentSqlVersion(String jdbcUrl, String userName, String password, String deriveClass) {
        Connection connect = null;
        try {
            connect = getConnection(jdbcUrl, userName, password, deriveClass);
            if (connect != null) {
                String queryVersionSQL = "select value from website where name = ?";
                PreparedStatement ps = connect.prepareStatement(queryVersionSQL);
                ps.setString(1, Constants.ZRLOG_SQL_VERSION_KEY);
                ResultSet resultSet = ps.executeQuery();
                if (resultSet.next()) {
                    String result = resultSet.getString(1);
                    ps.close();
                    return result;
                }
                ps.close();
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
        return "-1";
    }

    public static Connection getConnection(String jdbcUrl, String user, String password, String driverClass) {
        try {
            Class.forName(driverClass);
            return DriverManager.getConnection(jdbcUrl, user, password);
        } catch (ClassNotFoundException | SQLException e) {
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
        List<File> fileList = new ArrayList<>();
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

    public static List<Map.Entry<Integer, List<String>>> getExecSqlList(String sqlVersion, String basePath) {
        List<Map.Entry<Integer, List<String>>> sqlList = new ArrayList<>();
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
                    Map.Entry<Integer, List<String>> entry = new AbstractMap.SimpleEntry<>(fileVersion, Arrays.asList(IOUtil.getStringInputStream(new FileInputStream(f)).split("\n")));
                    sqlList.add(entry);
                }
            } catch (FileNotFoundException e) {
                LOGGER.error("", e);
            }
        }
        return sqlList;
    }


}
