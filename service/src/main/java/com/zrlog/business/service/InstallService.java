package com.zrlog.business.service;

import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.LoggerUtil;
import com.hibegin.common.util.SecurityUtils;
import com.zrlog.business.type.TestConnectDbResult;
import com.zrlog.util.DbConnectUtils;
import com.zrlog.common.Constants;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.ParseUtil;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 与安装向导相关的业务代码
 */
public class InstallService {

    private static final Logger LOGGER = LoggerUtil.getLogger(InstallService.class);
    private final String basePath;
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

    public static String renderMd(String md) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(md);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
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
        map.put("title", webSite.get("title"));
        map.put("second_title", webSite.get("second_title"));
        map.put("language", I18nUtil.getCurrentLocale());
        map.put(Constants.ZRLOG_SQL_VERSION_KEY, Constants.SQL_VERSION);
        return map;
    }

    /**
     * 尝试使用填写的数据库信息连接数据库
     */
    public TestConnectDbResult testDbConn() {
        TestConnectDbResult testConnectDbResult;
        Properties properties = new Properties();
        properties.putAll(dbConn);
        try (Connection connection = DbConnectUtils.getConnection(properties)) {
            LOGGER.info("Db connect success " + connection.getCatalog());
            testConnectDbResult = TestConnectDbResult.SUCCESS;
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "", e);
            testConnectDbResult = TestConnectDbResult.MISSING_MYSQL_DRIVER;
        } catch (SQLRecoverableException e) {
            LOGGER.log(Level.SEVERE, "", e);
            testConnectDbResult = TestConnectDbResult.CREATE_CONNECT_ERROR;
        } catch (SQLSyntaxErrorException e) {
            LOGGER.log(Level.SEVERE, "", e);
            testConnectDbResult = TestConnectDbResult.DB_NOT_EXISTS;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "", e);
            if (e.getMessage().contains("Access denied for user") && e.getMessage().contains("using password")) {
                testConnectDbResult = TestConnectDbResult.USERNAME_OR_PASSWORD_ERROR;
            } else {
                testConnectDbResult = TestConnectDbResult.SQL_EXCEPTION_UNKNOWN;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "", e);
            testConnectDbResult = TestConnectDbResult.UNKNOWN;
        }
        return testConnectDbResult;
    }

    /**
     * 通过执行数据库的sql文件，完成对数据库表，基础表数据的初始化，达到安装的效果
     *
     * @return false 表示安装没有正常执行，true 表示初始化数据库成功。
     */
    public boolean install() {
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
            Properties properties = new Properties();
            properties.putAll(dbConn);
            connect = DbConnectUtils.getConnection(properties);
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
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "install error ", e);
            lock.delete();
        } finally {
            try {
                connect.close();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "install error ", e);
            }
        }
        return false;
    }

    private void insertFirstArticle(Connection connect) throws SQLException {
        String insetLog = "INSERT INTO `log`(`logId`,`canComment`,`keywords`,`alias`,`typeId`,`userId`,`title`,`content`,`plain_content`,`markdown`,`digest`,`releaseTime`,`last_update_date`,`rubbish`,`privacy`) VALUES (1,?,?,?,1,1,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = connect.prepareStatement(insetLog)) {
            ps.setBoolean(1, true);
            String markdown = IOUtil.getStringInputStream(InstallService.class.getResourceAsStream("/init-blog/" + I18nUtil.getCurrentLocale() + ".md"));
            markdown = markdown.replace("${basePath}", "/");
            String content = renderMd(markdown);
            ps.setString(2, I18nUtil.getBlogStringFromRes("defaultType"));
            ps.setString(3, "hello-world");
            ps.setString(4, I18nUtil.getBlogStringFromRes("helloWorld"));
            ps.setString(5, content);
            ps.setString(6, VisitorArticleService.getPlainSearchText(content));
            ps.setString(7, markdown);
            ps.setString(8, ParseUtil.autoDigest(content, Constants.getAutoDigestLength()));
            ps.setObject(9, new java.util.Date());
            ps.setObject(10, new java.util.Date());
            ps.setBoolean(11, false);
            ps.setBoolean(12, false);
            ps.executeUpdate();
        }
    }

    private void insertType(Connection connect) throws SQLException {
        String insertLogType = "INSERT INTO `type`(`typeId`, `typeName`, `remark`, `alias`) VALUES (1,'" + I18nUtil.getBlogStringFromRes("defaultType") + "','','note')";
        try (PreparedStatement ps = connect.prepareStatement(insertLogType)) {
            ps.executeUpdate();
        }
    }

    private void insertTag(Connection connect) throws SQLException {
        String insertTag = "INSERT INTO `tag`(`tagId`,`text`,`count`) VALUES (1,'" + I18nUtil.getBlogStringFromRes("defaultType") + "',1)";
        try (PreparedStatement ps = connect.prepareStatement(insertTag)) {
            ps.executeUpdate();
        }
    }

    private void initPlugin(Connection connection) throws SQLException {
        String insertPluginSql = "INSERT INTO `plugin` VALUES (1,NULL,b'1','" + I18nUtil.getBlogStringFromRes("category") + "',NULL,'types',3),(2,NULL,b'1','" + I18nUtil.getBlogStringFromRes("tag") + "',NULL,'tags',3),(3,NULL,b'1','" + I18nUtil.getBlogStringFromRes("link") + "',NULL,'links',2),(4,NULL,b'1','" + I18nUtil.getBlogStringFromRes("archive") + "',NULL,'archives',3)";
        try (PreparedStatement ps = connection.prepareStatement(insertPluginSql)) {
            ps.executeUpdate();
        }
    }

    private void insertNav(Connection connect) throws SQLException {
        String insertLogNavSql = "INSERT INTO `lognav`( `navId`,`url`, `navName`, `sort`) VALUES (?,?,?,?)";
        try (PreparedStatement ps = connect.prepareStatement(insertLogNavSql)) {
            ps.setObject(1, 2);
            ps.setObject(2, Constants.ADMIN_LOGIN_URI_PATH);
            ps.setObject(3, I18nUtil.getBlogStringFromRes("manage"));
            ps.setObject(4, 2);
            ps.executeUpdate();
        }

        insertLogNavSql = "INSERT INTO `lognav`( `navId`,`url`, `navName`, `sort`) VALUES (?,?,?,?)";
        try (PreparedStatement ps = connect.prepareStatement(insertLogNavSql)) {
            ps.setObject(1, 1);
            ps.setObject(2, "/");
            ps.setObject(3, I18nUtil.getBlogStringFromRes("home"));
            ps.setObject(4, 1);
            ps.executeUpdate();
        }
    }

    private void initUser(Map<String, String> blogMsg, Connection connect) throws SQLException {
        String insertUserSql = "INSERT INTO `user`( `userId`,`userName`, `password`, `email`,`secretKey`) VALUES (1,?,?,?,?)";
        try (PreparedStatement ps = connect.prepareStatement(insertUserSql)) {
            ps.setString(1, blogMsg.get("username"));
            ps.setString(2, SecurityUtils.md5(blogMsg.get("password")));
            ps.setString(3, configMsg.get("email"));
            ps.setString(4, UUID.randomUUID().toString());
            ps.executeUpdate();
        }
    }

    private void initWebSite(Connection connect) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO `website` (`name`, `value`, `remark`) VALUES ");
        Map<String, Object> defaultMap = getDefaultWebSiteSettingMap(configMsg);
        for (int i = 0; i < defaultMap.size(); i++) {
            sb.append("(").append("?").append(",").append("?").append(",NULL),");
        }
        String insertWebSql = sb.substring(0, sb.toString().length() - 1);
        try (PreparedStatement ps = connect.prepareStatement(insertWebSql)) {
            int i = 1;
            for (Map.Entry<String, Object> e : defaultMap.entrySet()) {
                ps.setString(i++, e.getKey());
                ps.setObject(i++, e.getValue());
            }

            ps.executeUpdate();
        }
    }
}
