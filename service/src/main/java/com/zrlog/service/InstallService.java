package com.zrlog.service;

import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.SecurityUtils;
import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;
import com.zrlog.common.Constants;
import com.zrlog.common.type.TestConnectDbResult;
import com.zrlog.common.vo.InitFirstArticleVO;
import com.zrlog.util.BeanUtil;
import com.zrlog.util.ZrLogUtil;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.net.ConnectException;
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

    public InstallService(String basePath, Map<String, String> dbConn, Map<String, String> configMsg) {
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
    private Map<String, Object> getDefaultWebSiteSettingMap(Map<String, String> webSite) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("rows", 10);
        map.put("template", Constants.DEFAULT_TEMPLATE_PATH);
        map.put(Constants.AUTO_UPGRADE_VERSION_KEY, Constants.DEFAULT_AUTO_UPGRADE_VERSION_TYPE.getCycle());
        map.put("pseudo_static_status", false);
        map.put("title", webSite.get("title"));
        map.put("second_title", webSite.get("second_title"));
        map.put("home", webSite.get("home"));
        map.put(Constants.ZRLOG_SQL_VERSION_KEY, ZrLogUtil.getSqlVersion(basePath + "/update-sql"));
        return map;
    }

    /**
     * 尝试使用填写的数据库信息连接数据库
     */
    public TestConnectDbResult testDbConn() {
        TestConnectDbResult testConnectDbResult = null;
        try {
            Connection connection = getConnection();
            connection.close();
            testConnectDbResult = TestConnectDbResult.SUCCESS;
        } catch (ClassNotFoundException e) {
            LOGGER.error("", e);
            testConnectDbResult = TestConnectDbResult.MISSING_MYSQL_DRIVER;
        } catch (MySQLSyntaxErrorException e) {
            LOGGER.error("", e);
            if (e.getMessage().contains("Access denied for user ")) {
                testConnectDbResult = TestConnectDbResult.DB_NOT_EXISTS;
            }
        } catch (SQLException e) {
            LOGGER.error("", e);
            if (e.getCause() instanceof ConnectException) {
                testConnectDbResult = TestConnectDbResult.CREATE_CONNECT_ERROR;
            } else {
                testConnectDbResult = TestConnectDbResult.USERNAME_OR_PASSWORD_ERROR;
            }
        } catch (Exception e) {
            LOGGER.error("", e);
            testConnectDbResult = TestConnectDbResult.UNKNOWN;
        }
        return testConnectDbResult;
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

    private boolean startInstall(Map<String, String> dbConn, Map<String, String> blogMsg, File lock) {
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
        try (FileOutputStream out = new FileOutputStream(file)) {
            lock.createNewFile();
            file.createNewFile();
            Properties prop = new Properties();
            prop.putAll(dbConn);
            prop.store(out, "This is a database configuration file");
            out.close();
            String s = IOUtil.getStringInputStream(InstallService.class.getResourceAsStream("/init-table-structure.sql"));
            String[] sql = s.split("\n");
            StringBuilder tempSqlStr = new StringBuilder();
            for (String sqlSt : sql) {
                if (sqlSt.startsWith("#") || sqlSt.startsWith("/*")) {
                    continue;
                }
                tempSqlStr.append(sqlSt);
            }
            sql = tempSqlStr.toString().split(";");
            for (String sqlSt : sql) {
                if (!"".equals(sqlSt)) {
                    st = connect.createStatement();
                    st.execute(sqlSt);
                    st.close();
                }

            }

            //初始数据
            initWebSite(connect);
            initUser(blogMsg, connect);
            insertNav(connect);
            initPlugin(connect);
            insertType(connect);
            insertTag(connect);
            insertFirstArticle(connect);

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
        }
        return false;
    }


    private void insertFirstArticle(Connection connect) throws SQLException {
        String insetLog = "INSERT INTO `log`(`logId`,`canComment`,`keywords`,`alias`,`typeId`,`userId`,`title`,`content`,`plain_content`,`mdContent`,`digest`,`releaseTime`,`last_update_date`,`rubbish`,`private`) VALUES (1,?,?,?,1,1,?,?,?,?,?,?,?,?,?)";
        PreparedStatement ps = connect.prepareStatement(insetLog);
        ps.setBoolean(1, true);
        InitFirstArticleVO initFirstArticleVO = BeanUtil.convert(InstallService.class.getResourceAsStream("/init-blog.json"), InitFirstArticleVO.class);
        ps.setString(2, initFirstArticleVO.getKeywords());
        ps.setString(3, initFirstArticleVO.getAlias());
        ps.setString(4, initFirstArticleVO.getTitle());
        ps.setString(5, initFirstArticleVO.getContent());
        ps.setString(6, initFirstArticleVO.getPlainContent());
        ps.setString(7, initFirstArticleVO.getMdContent());
        ps.setString(8, initFirstArticleVO.getDigest());
        ps.setObject(9, new java.util.Date());
        ps.setObject(10, new java.util.Date());
        ps.setBoolean(11, false);
        ps.setBoolean(12, false);
        ps.executeUpdate();
        ps.close();
    }

    private void insertType(Connection connect) throws SQLException {
        String insertLogType = "INSERT INTO `type`(`typeId`, `typeName`, `remark`, `alias`) VALUES (1,'记录','','notes')";
        PreparedStatement ps = connect.prepareStatement(insertLogType);
        ps.executeUpdate();
        ps.close();
    }

    private void insertTag(Connection connect) throws SQLException {
        String insertTag = "INSERT INTO `tag`(`tagId`,`text`,`count`) VALUES (1,'记录',1)";
        PreparedStatement ps = connect.prepareStatement(insertTag);
        ps.executeUpdate();
        ps.close();
    }

    private void initPlugin(Connection connection) throws SQLException {
        String insertPluginSql = "INSERT INTO `plugin` VALUES (1,'系统提供的插件',b'1','分类菜单',NULL,'types',3),(2,NULL,b'1','标签云',NULL,'tags',3),(3,NULL,b'1','友链',NULL,'links',2),(4,NULL,b'1','存档',NULL,'archives',3)";
        PreparedStatement ps = connection.prepareStatement(insertPluginSql);
        ps.executeUpdate();
        ps.close();
    }

    private void insertNav(Connection connect) throws SQLException {
        String insertLogNavSql = "INSERT INTO `lognav`( `navId`,`url`, `navName`, `sort`) VALUES (?,?,?,?)";
        PreparedStatement ps = connect.prepareStatement(insertLogNavSql);
        ps.setObject(1, 2);
        ps.setObject(2, "/admin/login");
        ps.setObject(3, "管理");
        ps.setObject(4, 2);
        ps.executeUpdate();
        ps.close();

        insertLogNavSql = "INSERT INTO `lognav`( `navId`,`url`, `navName`, `sort`) VALUES (?,?,?,?)";
        ps = connect.prepareStatement(insertLogNavSql);
        ps.setObject(1, 1);
        ps.setObject(2, "/");
        ps.setObject(3, "主页");
        ps.setObject(4, 1);
        ps.executeUpdate();
        ps.close();
    }

    private void initUser(Map<String, String> blogMsg, Connection connect) throws SQLException {
        String insertUserSql = "INSERT INTO `user`( `userId`,`userName`, `password`, `email`,`secretKey`) VALUES (1,?,?,?,?)";
        PreparedStatement ps = connect.prepareStatement(insertUserSql);
        ps.setString(1, blogMsg.get("username"));
        ps.setString(2, SecurityUtils.md5(blogMsg.get("password")));
        ps.setString(3, configMsg.get("email"));
        ps.setString(4, UUID.randomUUID().toString());
        ps.executeUpdate();
        ps.close();
    }

    private void initWebSite(Connection connect) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO `website` (`name`, `value`, `remark`) VALUES ");
        Map<String, Object> defaultMap = getDefaultWebSiteSettingMap(configMsg);
        for (int i = 0; i < defaultMap.size(); i++) {
            sb.append("(").append("?").append(",").append("?").append(",NULL),");
        }
        String insertWebSql = sb.toString().substring(0, sb.toString().length() - 1);
        PreparedStatement ps = connect.prepareStatement(insertWebSql);
        int i = 1;
        for (Map.Entry<String, Object> e : defaultMap.entrySet()) {
            ps.setString(i++, e.getKey());
            ps.setObject(i++, e.getValue());
        }

        ps.executeUpdate();
        ps.close();
    }
}
