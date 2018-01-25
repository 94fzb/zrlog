package com.zrlog.service;

import com.hibegin.common.util.http.HttpUtil;
import com.hibegin.common.util.http.handle.HttpJsonArrayHandle;
import com.zrlog.util.ZrlogUtil;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadService {

    private static final Logger LOGGER = Logger.getLogger(UploadService.class);

    public String getCloudUrl(String contextPath, String uri, String finalFilePath, HttpServletRequest request) {
        // try push to cloud
        Map<String, String[]> map = new HashMap<>();
        map.put("fileInfo", new String[]{finalFilePath + "," + uri});
        map.put("name", new String[]{"uploadService"});
        String url;
        try {
            List<Map> urls = HttpUtil.getInstance().sendGetRequest(ZrlogUtil.getPluginServer() + "/service", map
                    , new HttpJsonArrayHandle<Map>(), ZrlogUtil.genHeaderMapByRequest(request)).getT();
            if (urls != null && !urls.isEmpty()) {
                url = (String) urls.get(0).get("url");
            } else {
                url = contextPath + uri;
            }
        } catch (Exception e) {
            url = contextPath + uri;
            LOGGER.error(e);
        }
        return url;
    }

}
