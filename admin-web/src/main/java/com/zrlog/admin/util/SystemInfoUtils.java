package com.zrlog.admin.util;

import com.hibegin.http.server.api.HttpRequest;
import com.zrlog.business.util.InstallUtils;
import com.zrlog.common.Constants;
import com.zrlog.common.vo.ServerInfo;
import com.zrlog.util.BlogBuildInfoUtil;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SystemInfoUtils {


    public static List<ServerInfo> serverInfo(HttpRequest httpRequest) throws SQLException {
        Map<String, Object> info = new HashMap<>();
        DataSource dataSource = Constants.zrLogConfig.getDataSource();
        if (Objects.nonNull(dataSource)) {
            InstallUtils.getSystemProp(dataSource.getConnection()).forEach((key, value) -> info.put(key.toString(), value));
        } else {
            InstallUtils.getSystemProp(null).forEach((key, value) -> info.put(key.toString(), value));
        }
        BlogBuildInfoUtil.getBlogProp().forEach((key, value) -> info.put("zrlog." + key.toString(), value));
        String applicationName = httpRequest.getServerConfig().getApplicationName();
        if (applicationName.startsWith("zrlog")) {
            info.put("server.info", com.hibegin.http.server.util.ServerInfo.getName() + "/" + com.hibegin.http.server.util.ServerInfo.getVersion());
        } else {
            info.put("server.info", applicationName + "/" + httpRequest.getServerConfig().getApplicationVersion());
        }
        return ServerInfoUtils.convertToServerInfos(info);
    }

    public static List<ServerInfo> systemIOInfoVO() {
        return ServerInfoUtils.getServerInfos2();
    }
}
