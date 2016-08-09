package com.fzb.blog.util;

import com.fzb.blog.config.ZrlogConfig;
import com.fzb.blog.controller.BaseController;
import com.fzb.common.util.IOUtil;
import com.fzb.common.util.Md5Util;
import com.jfinal.config.Plugins;
import com.jfinal.core.JFinal;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class InstallUtil {

    private static final Logger LOGGER = Logger.getLogger(InstallUtil.class);
    private String basePath;
    private Map<String, String> dbConn;
    private Map<String, String> configMsg;

    public InstallUtil(String basePath) {
        this.basePath = basePath;
    }

    public InstallUtil(String basePath, Map<String, String> dbConn,
                       Map<String, String> configMsg) {
        this.basePath = basePath;
        this.dbConn = dbConn;
        this.configMsg = configMsg;
    }

    public InstallUtil(String basePath, Map<String, String> dbConn) {
        this.basePath = basePath;
        this.dbConn = dbConn;
    }

    private static Map<String, Object> defaultWebSite(Map<String, String> webSite) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("rows", 10);
        map.put("template", BaseController.getDefaultTemplatePath());
        map.put("pseudo_staticStatus", false);
        map.put("title", webSite.get("title"));
        map.put("second_title", webSite.get("second_title"));
        map.put("home", webSite.get("home"));
        return map;
    }

    public boolean testDbConn() {
        try {
            Class.forName(dbConn.get("driverClass"));
            Connection connect = DriverManager.getConnection(dbConn.get("jdbcUrl"), dbConn.get("user"), dbConn.get("password"));
            connect.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public Boolean install() {
        File lock = new File(basePath + "/install.lock");
        return !lock.exists() && startInstall(dbConn, configMsg, lock);
    }

    public boolean checkInstall() {
        File lock = new File(basePath + "/install.lock");
        return lock.exists();
    }

    private boolean startInstall(Map<String, String> dbConn,
                                Map<String, String> blogMsg, File lock) {
        C3p0Plugin c3p0Plugin = new C3p0Plugin(dbConn.get("jdbcUrl"), dbConn.get("user"),
                dbConn.get("password"), dbConn.get("driverClass"));
        Connection connect;
        try {
            c3p0Plugin.start();
            connect = c3p0Plugin.getDataSource().getConnection();
        } catch (SQLException e) {
            return false;
        }

        File file = new File(basePath + "/db.properties");
        Statement st;
        if (file.exists()) {
            file.delete();
        }
        try {
            lock.createNewFile();
            file.createNewFile();
            OutputStream out = new FileOutputStream(file);
            Properties prop = new Properties();
            prop.putAll(dbConn);
            prop.store(out, "This is a database configuration file");
            out.close();
            File sqlFile = new File(basePath + "/install.sql");
            String s = IOUtil.getStringInputStream(new FileInputStream(sqlFile));
            String[] sql = s.split("\n");
            String tempSqlStr = "";
            for (String sqlSt : sql) {
                if (sqlSt.startsWith("#") || sqlSt.startsWith("/*")) {
                    continue;
                }
                tempSqlStr += sqlSt;
            }
            sql = tempSqlStr.split(";");
            for (String sqlSt : sql) {
                if (!"".equals(sqlSt)) {
                    st = connect.createStatement();
                    st.execute(sqlSt);
                }

            }
            StringBuilder sb = new StringBuilder();
            sb.append("INSERT INTO `website` (`name`, `value`, `remark`) VALUES ");
            Map<String, Object> defaultMap = defaultWebSite(configMsg);
            for (Map.Entry e : defaultMap.entrySet()) {
                sb.append("('").append(e.getKey()).append("','").append(e.getValue()).append("',NULL),");
            }

            String insertWebSql = sb.toString().substring(0, sb.toString().length() - 1);

            PreparedStatement ps = connect.prepareStatement(insertWebSql);
            ps.executeUpdate();
            String insertUserSql = "INSERT INTO `user`( `userId`,`userName`, `password`, `email`) VALUES (1,?,?,?)";
            ps = connect.prepareStatement(insertUserSql);
            ps.setString(1, blogMsg.get("username"));
            ps.setString(2, Md5Util.MD5(blogMsg.get("password")));
            ps.setString(3, configMsg.get("email"));
            ps.executeUpdate();

            String insertLogNavSql = "INSERT INTO `lognav`( `navId`,`url`, `navName`, `sort`) VALUES (?,?,?,?)";
            ps = connect.prepareStatement(insertLogNavSql);
            ps.setObject(1, 1);
            ps.setObject(2, "/");
            ps.setObject(3, "主页");
            ps.setObject(4, 1);
            ps.executeUpdate();

            insertLogNavSql = "INSERT INTO `lognav`( `navId`,`url`, `navName`, `sort`) VALUES (?,?,?,?)";
            ps = connect.prepareStatement(insertLogNavSql);
            ps.setObject(1, 2);
            ps.setObject(2, "/admin/login");
            ps.setObject(3, "管理");
            ps.setObject(4, 2);
            ps.executeUpdate();

            String insertPluginSql = "INSERT INTO `plugin` VALUES (1,'系统提供的插件',b'1','分类菜单',NULL,'types',3),(2,NULL,b'1','标签云',NULL,'tags',3),(3,NULL,b'1','友链',NULL,'links',2),(4,NULL,b'1','存档',NULL,'archives',3)";
            ps = connect.prepareStatement(insertPluginSql);
            ps.executeUpdate();

            String insertLogType = "INSERT INTO `type`(`typeId`, `typeName`, `remark`, `alias`) VALUES (1,'记录','','notes')";
            ps.execute(insertLogType);
            String insetLog = "INSERT INTO `log`(`logId`,`canComment`,`keywords`,`alias`,`typeId`,`userId`,`title`,`content`,`digest`,`releaseTime`,`rubbish`,`private`) VALUES (1,?,'记录','first',1,1,'记录学习记录','每天进步一点','每天进步一点',?,?,?)";
            ps = connect.prepareStatement(insetLog);
            ps.setBoolean(1, true);
            ps.setObject(2, new java.util.Date());
            ps.setBoolean(3, false);
            ps.setBoolean(4, false);
            ps.executeUpdate();

            String insertTag = "INSERT INTO `tag`(`tagId`,`text`,`count`) VALUES (1,'记录',1)";
            ps = connect.prepareStatement(insertTag);
            ps.executeUpdate();
            LOGGER.info("reRegister c3p0");
            Plugins plugins = (Plugins) JFinal.me().getServletContext().getAttribute("plugins");

            // 添加表与实体的映射关系
            ActiveRecordPlugin plugin = ZrlogConfig.getActiveRecordPlugin(c3p0Plugin, file.toString());
            plugin.start();
            plugins.add(plugin);
            return true;
        } catch (Exception e) {
            LOGGER.error("install error ", e);
            lock.delete();
        } finally {
            try {
                connect.close();
            } catch (SQLException e) {
                LOGGER.error("install error ", e);
            }
        }
        return false;
    }
}
