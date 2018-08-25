package com.zrlog.web.plugin;

import com.jfinal.plugin.IPlugin;
import com.jfinal.plugin.activerecord.IDataSourceProvider;
import org.vibur.dbcp.ViburDBCPDataSource;

import javax.sql.DataSource;

public class ViburDbcpPlugin implements IPlugin, IDataSourceProvider {

    private ViburDBCPDataSource ds;

    private String jdbcUrl;
    private String username;
    private String password;

    public ViburDbcpPlugin(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }

    @Override
    public boolean start() {
        ds = new ViburDBCPDataSource();
        ds.setJdbcUrl(jdbcUrl);
        ds.setPassword(password);
        ds.setUsername(username);
        ds.start();
        return true;
    }

    @Override
    public boolean stop() {
        ds.close();
        return true;
    }

    @Override
    public DataSource getDataSource() {
        return ds;
    }
}
