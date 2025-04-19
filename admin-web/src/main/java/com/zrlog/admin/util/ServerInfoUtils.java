package com.zrlog.admin.util;

import com.hibegin.common.util.FileUtils;
import com.hibegin.common.util.LoggerUtil;
import com.hibegin.common.util.StringUtils;
import com.hibegin.http.server.util.PathUtil;
import com.sun.management.OperatingSystemMXBean;
import com.zrlog.admin.web.controller.api.AdminController;
import com.zrlog.common.Constants;
import com.zrlog.common.vo.ServerInfo;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.I18nUtil;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.text.DecimalFormat;
import java.util.*;

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
        systemInfo.add(new ServerInfo(I18nUtil.getBackendStringFromRes("serverInfo.cpuInfo"), CPUInfo.instance.getCpuModel(), "cpuInfo"));
        systemInfo.add(new ServerInfo(I18nUtil.getBackendStringFromRes("serverInfo.encoding"), (String) data.get("file.encoding"), "encoding"));
        systemInfo.add(new ServerInfo(I18nUtil.getBackendStringFromRes("serverInfo.programInfo"), BlogBuildInfoUtil.getVersionInfo(), "programInfo"));
        return systemInfo;
    }

    public static List<ServerInfo> getServerInfos2() {
        List<File> allFileList = new ArrayList<>();
        List<ServerInfo> systemInfo = new ArrayList<>();

        try {
            List<String> baseFolders = new ArrayList<>(Arrays.asList(PathUtil.getTempPath(),
                    PathUtil.getLogPath(), PathUtil.getConfPath(), PathUtil.getStaticPath(),
                    PathUtil.getRootPath() + "/doc",
                    PathUtil.getRootPath() + "/LICENSE",
                    PathUtil.getRootPath() + "/README.en-us.md",
                    PathUtil.getRootPath() + "/README.md",
                    PathUtil.getRootPath() + "/bin",
                    PathUtil.getRootPath() + "/lib"
            ));
            if (Objects.nonNull(Constants.zrLogConfig.getUpdater())) {
                allFileList.add(Constants.zrLogConfig.getUpdater().execFile());
            }
            for (String folder : baseFolders) {
                FileUtils.getAllFiles(folder, allFileList);
            }
            List<File> cacheFileList = new ArrayList<>();
            FileUtils.getAllFiles(PathUtil.getCachePath(), cacheFileList);
            allFileList.addAll(cacheFileList);
            // 获取堆内存的使用情况
            OperatingSystemMXBean osMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
            MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
            MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();

            systemInfo.add(new ServerInfo(I18nUtil.getBackendStringFromRes("serverInfo.usedCacheSpace"), formatFileSize(cacheFileList.stream().mapToLong(File::length).sum()), "usedCacheSpace"));
            systemInfo.add(new ServerInfo(I18nUtil.getBackendStringFromRes("serverInfo.usedDiskSpace"), formatFileSize(allFileList.stream().mapToLong(File::length).sum()), "usedDiskSpace"));
            systemInfo.add(new ServerInfo(I18nUtil.getBackendStringFromRes("serverInfo.usedMemorySpace"), formatFileSize(heapMemoryUsage.getUsed()), "usedMemorySpace"));
            systemInfo.add(new ServerInfo(I18nUtil.getBackendStringFromRes("serverInfo.totalMemorySpace"), formatFileSize(osMXBean.getTotalMemorySize()), "totalMemorySpace"));
            systemInfo.add(new ServerInfo(I18nUtil.getBackendStringFromRes("serverInfo.cpuLoad"), CPUInfo.instance.getCpuLoad(), "cpuLoad"));
            systemInfo.add(new ServerInfo(I18nUtil.getBackendStringFromRes("serverInfo.systemLoad"), SystemLoad.getSystemLoad(), "systemLoad"));
            systemInfo.add(new ServerInfo(I18nUtil.getBackendStringFromRes("serverInfo.dbConnectSize"),
                    Constants.zrLogConfig.getDatabaseConnectPoolInfo().connectActiveSize() + " / " + Constants.zrLogConfig.getDatabaseConnectPoolInfo().connectTotalSize(), "dbConnectSize"));
            systemInfo.add(new ServerInfo(I18nUtil.getBackendStringFromRes("serverInfo.uptime"), Constants.zrLogConfig.getProgramUptime(), "uptime"));
            return systemInfo;
        } catch (Exception e) {
            LoggerUtil.getLogger(AdminController.class).warning("Load server info error " + e.getMessage());
        }
        return systemInfo;
    }

    private static String formatFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString;
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else if (fileS < 1099511627776L) {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        } else {
            fileSizeString = df.format((double) fileS / 1099511627776L) + "T";
        }
        return fileSizeString;
    }

}


