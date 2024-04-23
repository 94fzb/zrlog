package com.zrlog.util;

import com.hibegin.common.util.StringUtils;
import com.zrlog.common.vo.ServerInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ServerInfoUtils {
    public static List<ServerInfo> convertToServerInfos(Map<String, Object> data) {
        List<ServerInfo> systemInfo = new ArrayList<>();
        systemInfo.add(new ServerInfo(I18nUtil.getBackendStringFromRes("serverInfo.runtime"), data.get("java.vm.name") + " - " + data.get("java.runtime.version")));
        systemInfo.add(new ServerInfo(I18nUtil.getBackendStringFromRes("serverInfo.webServer"), (String) data.get("server.info")));
        systemInfo.add(new ServerInfo(I18nUtil.getBackendStringFromRes("serverInfo.runPath"), (String) data.get("zrlog.runtime.path")));
        systemInfo.add(new ServerInfo(I18nUtil.getBackendStringFromRes("serverInfo.system"), data.get("os.name") + " - " + data.get("os.arch") + " - " + data.get("os.version")));
        systemInfo.add(new ServerInfo(I18nUtil.getBackendStringFromRes("serverInfo.timezone"), (String) data.get("user.timezone")));
        Locale locale = Locale.getDefault();
        systemInfo.add(new ServerInfo(I18nUtil.getBackendStringFromRes("serverInfo.locale"), locale.getLanguage() + "/" + (StringUtils.isNotEmpty(locale.getCountry()) ? locale.getCountry() : "Unknown")));
        systemInfo.add(new ServerInfo(I18nUtil.getBackendStringFromRes("serverInfo.dbInfo"), (String) data.get("dbServer.version")));
        systemInfo.add(new ServerInfo(I18nUtil.getBackendStringFromRes("serverInfo.encoding"), (String) data.get("file.encoding")));
        systemInfo.add(new ServerInfo(I18nUtil.getBackendStringFromRes("serverInfo.programInfo"), data.get("zrlog.version") + " - " + data.get("zrlog.buildId") + " (" + data.get("zrlog.buildTime") + ")"));
        return systemInfo;
    }
}


