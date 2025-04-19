package com.zrlog.admin.util;

import com.hibegin.common.util.FileUtils;
import com.hibegin.common.util.LoggerUtil;
import com.hibegin.http.server.util.PathUtil;
import com.sun.management.OperatingSystemMXBean;
import com.zrlog.admin.business.rest.response.SystemIOInfoVO;
import com.zrlog.admin.web.controller.api.AdminController;
import com.zrlog.business.util.InstallUtils;
import com.zrlog.common.Constants;
import com.zrlog.common.vo.ServerInfo;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.ServerInfoUtils;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.*;

public class SystemInfoUtils {


    public static List<ServerInfo> serverInfo() {
        Map<String, Object> info = new HashMap<>();
        InstallUtils.getSystemProp().forEach((key, value) -> info.put(key.toString(), value));
        BlogBuildInfoUtil.getBlogProp().forEach((key, value) -> info.put("zrlog." + key.toString(), value));
        return ServerInfoUtils.convertToServerInfos(info);
    }

    public static SystemIOInfoVO systemIOInfoVO() {
        List<File> allFileList = new ArrayList<>();
        SystemIOInfoVO systemDiskInfoVO = new SystemIOInfoVO();
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
            systemDiskInfoVO.setUsedDiskSpace(allFileList.stream().mapToLong(File::length).sum());
            systemDiskInfoVO.setUsedCacheSpace(cacheFileList.stream().mapToLong(File::length).sum());

            MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

            // 获取堆内存的使用情况
            MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
            systemDiskInfoVO.setUsedMemorySpace(heapMemoryUsage.getUsed());
            OperatingSystemMXBean osMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
            systemDiskInfoVO.setTotalMemorySpace(osMXBean.getTotalMemorySize());
        } catch (Exception e) {
            LoggerUtil.getLogger(AdminController.class).warning("Load used disk info error " + e.getMessage());
            systemDiskInfoVO.setUsedCacheSpace(-1L);
            systemDiskInfoVO.setUsedDiskSpace(-1L);
        }
        return systemDiskInfoVO;
    }
}
