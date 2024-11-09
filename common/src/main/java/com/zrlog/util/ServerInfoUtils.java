package com.zrlog.util;

import com.hibegin.common.util.StringUtils;
import com.zrlog.common.Constants;
import com.zrlog.common.ZrLogConfig;
import com.zrlog.common.vo.ServerInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ServerInfoUtils {
    public static List<ServerInfo> convertToServerInfos(Map<String, Object> data) {
        List<ServerInfo> systemInfo = new ArrayList<>();
        systemInfo.add(new ServerInfo(I18nUtil.getBackendStringFromRes("serverInfo.system"), Constants.getRealFileArch() + " - " + data.get("os.version"), "system"));
        systemInfo.add(new ServerInfo(I18nUtil.getBackendStringFromRes("serverInfo.runPath"), (String) data.get("zrlog.runtime.path"), "runPath"));
        systemInfo.add(new ServerInfo(I18nUtil.getBackendStringFromRes("serverInfo.runtime"), data.get("java.vm.name") + " - " + data.get("java.version"), "runtime"));
        systemInfo.add(new ServerInfo(I18nUtil.getBackendStringFromRes("serverInfo.webServer"), (String) data.get("server.info"), "webServer"));
        systemInfo.add(new ServerInfo(I18nUtil.getBackendStringFromRes("serverInfo.timezone"), (String) data.get("user.timezone"), "timezone"));
        Locale locale = Locale.getDefault();
        systemInfo.add(new ServerInfo(I18nUtil.getBackendStringFromRes("serverInfo.locale"), locale.getLanguage() + "/" + (StringUtils.isNotEmpty(locale.getCountry()) ? locale.getCountry() : "Unknown"), "locale"));
        systemInfo.add(new ServerInfo(I18nUtil.getBackendStringFromRes("serverInfo.dbInfo"), (String) data.get("dbServer.version"), "dbInfo"));
        systemInfo.add(new ServerInfo(I18nUtil.getBackendStringFromRes("serverInfo.encoding"), (String) data.get("file.encoding"), "encoding"));
        systemInfo.add(new ServerInfo(I18nUtil.getBackendStringFromRes("serverInfo.uptime"), Constants.zrLogConfig.getProgramUptime(), "uptime"));
        systemInfo.add(new ServerInfo(I18nUtil.getBackendStringFromRes("serverInfo.programInfo"), BlogBuildInfoUtil.getVersionInfo(), "programInfo"));
        return systemInfo;
    }
}


