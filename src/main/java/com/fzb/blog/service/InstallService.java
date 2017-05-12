package com.fzb.blog.service;

import com.fzb.blog.common.Constants;
import com.fzb.blog.util.ZrlogUtil;
import com.fzb.common.util.IOUtil;
import com.fzb.common.util.SecurityUtils;
import com.jfinal.kit.PathKit;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

/**
 * 与安装向导相关的业务代码
 */
public class InstallService {

    private static final Logger LOGGER = Logger.getLogger(InstallService.class);
    private String basePath;
    private Map<String, String> dbConn;
    private Map<String, String> configMsg;

    public InstallService(String basePath) {
        this.basePath = basePath;
    }

    public InstallService(String basePath, Map<String, String> dbConn,
                          Map<String, String> configMsg) {
        this.basePath = basePath;
        this.dbConn = dbConn;
        this.configMsg = configMsg;
    }

    public InstallService(String basePath, Map<String, String> dbConn) {
        this.basePath = basePath;
        this.dbConn = dbConn;
    }

    /**
     * 封装网站设置的数据数据，返回Map形式方便调用者进行遍历
     *
     * @param webSite
     * @return
     */
    private static Map<String, Object> getDefaultWebSiteSettingMap(Map<String, String> webSite) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("rows", 10);
        map.put("template", Constants.DEFAULT_TEMPLATE_PATH);
        map.put(Constants.AUTO_UPGRADE_VERSION_KEY, Constants.DEFAULT_AUTO_UPGRADE_VERSION_TYPE.getCycle());
        map.put("pseudo_staticStatus", false);
        map.put("title", webSite.get("title"));
        map.put("second_title", webSite.get("second_title"));
        map.put("home", webSite.get("home"));
        map.put(Constants.ZRLOG_SQL_VERSION_KEY, ZrlogUtil.getSqlVersion(PathKit.getWebRootPath() + "/WEB-INF/update-sql"));
        return map;
    }

    /**
     * 尝试使用填写的数据库信息连接数据库
     *
     * @return false 表示建立连接失败，true 表示成功
     */
    public boolean testDbConn() {
        try {
            Connection connection = getConnection();
            connection.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName(dbConn.get("driverClass"));
        return DriverManager.getConnection(dbConn.get("jdbcUrl"), dbConn.get("user"), dbConn.get("password"));
    }

    /**
     * 通过执行数据库的sql文件，完成对数据库表，基础表数据的初始化，达到安装的效果
     *
     * @return false 表示安装没有正常执行，true 表示初始化数据库成功。
     */
    public Boolean install() {
        File lock = new File(basePath + "/install.lock");
        return !lock.exists() && startInstall(dbConn, configMsg, lock);
    }

    /**
     * 通过检查特定目录下面是否存在 install.lock 文件，进行判断是否已经完成安装
     *
     * @return
     */
    public boolean checkInstall() {
        File lock = new File(basePath + "/install.lock");
        return lock.exists();
    }

    private boolean startInstall(Map<String, String> dbConn,
                                 Map<String, String> blogMsg, File lock) {
        Connection connect;
        try {
            connect = getConnection();
        } catch (Exception e) {
            return false;
        }

        File file = new File(basePath + "/db.properties");
        Statement st;
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            lock.createNewFile();
            file.createNewFile();
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
            Map<String, Object> defaultMap = getDefaultWebSiteSettingMap(configMsg);
            for (Map.Entry e : defaultMap.entrySet()) {
                sb.append("('").append(e.getKey()).append("','").append(e.getValue()).append("',NULL),");
            }

            String insertWebSql = sb.toString().substring(0, sb.toString().length() - 1);

            PreparedStatement ps = connect.prepareStatement(insertWebSql);
            ps.executeUpdate();
            String insertUserSql = "INSERT INTO `user`( `userId`,`userName`, `password`, `email`,`secretKey`) VALUES (1,?,?,?,?)";
            ps = connect.prepareStatement(insertUserSql);
            ps.setString(1, blogMsg.get("username"));
            ps.setString(2, SecurityUtils.md5(blogMsg.get("password")));
            ps.setString(3, configMsg.get("email"));
            ps.setString(4, UUID.randomUUID().toString());
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
            out.close();
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
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    LOGGER.error("close stream error", e);
                }
            }
        }
        return false;
    }
}
