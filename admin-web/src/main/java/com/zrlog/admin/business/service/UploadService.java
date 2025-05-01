package com.zrlog.admin.business.service;

import com.hibegin.common.util.LoggerUtil;
import com.hibegin.http.server.api.HttpRequest;
import com.zrlog.admin.business.rest.response.UploadFileResponse;
import com.zrlog.admin.plugin.rest.response.UploadServiceResponseEntity;
import com.zrlog.business.plugin.PluginCorePlugin;
import com.zrlog.common.Constants;
import com.zrlog.common.vo.AdminTokenVO;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UploadService {

    private static final Logger LOGGER = LoggerUtil.getLogger(UploadService.class);

    public UploadFileResponse getCloudUrl(String contextPath, String uri, String finalFilePath, HttpRequest request, AdminTokenVO adminTokenVO) {
        // try push to cloud
        String url;
        try {
            Map<String, String[]> uploadParams = new HashMap<>();
            uploadParams.put("fileInfo", new String[]{finalFilePath + "," + uri});
            uploadParams.put("name", new String[]{"uploadService"});

            PluginCorePlugin pluginCorePlugin = Constants.zrLogConfig.getPlugin(PluginCorePlugin.class);
            UploadServiceResponseEntity[] urls = pluginCorePlugin.requestService(request,
                    uploadParams, adminTokenVO, UploadServiceResponseEntity[].class);
            if (urls != null && urls.length > 0) {
                url = urls[0].getUrl();
                if (!url.startsWith("https://") && !url.startsWith("http://")) {
                    String tUrl = url;
                    if (!url.startsWith("/")) {
                        tUrl = "/" + url;
                    }
                    url = contextPath + tUrl;
                }
            } else {
                url = contextPath + uri;
            }
        } catch (Exception e) {
            url = contextPath + uri;
            LOGGER.log(Level.SEVERE, "", e);
        }
        return new UploadFileResponse(url);
    }
}
