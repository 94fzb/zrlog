package com.zrlog.admin.util;

import com.hibegin.http.server.api.HttpRequest;
import com.zrlog.business.util.InstallUtils;
import com.zrlog.common.vo.ServerInfo;
import com.zrlog.util.BlogBuildInfoUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SystemInfoUtils {


    public static List<ServerInfo> serverInfo(HttpRequest httpRequest) {
        Map<String, Object> info = new HashMap<>();
        InstallUtils.getSystemProp().forEach((key, value) -> info.put(key.toString(), value));
        BlogBuildInfoUtil.getBlogProp().forEach((key, value) -> info.put("zrlog." + key.toString(), value));
        info.put("server.info", httpRequest.getServerConfig().getApplicationName() + "/" + httpRequest.getServerConfig().getApplicationVersion());
        return ServerInfoUtils.convertToServerInfos(info);
    }

    public static List<ServerInfo> systemIOInfoVO() {
        return ServerInfoUtils.getServerInfos2();
    }
}
