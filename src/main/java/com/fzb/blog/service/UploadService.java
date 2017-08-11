package com.fzb.blog.service;

import com.fzb.blog.util.ZrlogUtil;
import com.fzb.common.util.http.HttpUtil;
import com.fzb.common.util.http.handle.HttpJsonArrayHandle;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
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
        } catch (IOException e) {
            url = contextPath + uri;
            LOGGER.error(e);
        }
        return url;
    }

}
