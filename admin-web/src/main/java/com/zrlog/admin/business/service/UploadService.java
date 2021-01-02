package com.zrlog.admin.business.service;

import com.hibegin.common.util.http.HttpUtil;
import com.hibegin.common.util.http.handle.HttpJsonArrayHandle;
import com.zrlog.admin.business.rest.response.UploadFileResponse;
import com.zrlog.business.util.PluginHelper;
import com.zrlog.common.Constants;
import com.zrlog.common.vo.AdminTokenVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadService.class);

    public UploadFileResponse getCloudUrl(String contextPath, String uri, String finalFilePath, HttpServletRequest request, AdminTokenVO adminTokenVO) {
        UploadFileResponse uploadFileResponse = new UploadFileResponse();
        // try push to cloud
        Map<String, String[]> map = new HashMap<>();
        map.put("fileInfo", new String[]{finalFilePath + "," + uri});
        map.put("name", new String[]{"uploadService"});
        String url;
        try {
            List<Map> urls = HttpUtil.getInstance().sendGetRequest(Constants.pluginServer + "/service", map
                    , new HttpJsonArrayHandle<Map>(), PluginHelper.genHeaderMapByRequest(request, adminTokenVO)).getT();
            if (urls != null && !urls.isEmpty()) {
                url = (String) urls.get(0).get("url");
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
            LOGGER.error("", e);
        }
        uploadFileResponse.setUrl(url);
        return uploadFileResponse;
    }
}
