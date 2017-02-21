package com.fzb.blog.web.controller.admin.api;

import com.fzb.blog.common.response.UploadFileResponse;
import com.fzb.blog.util.ZrlogUtil;
import com.fzb.blog.web.controller.BaseController;
import com.fzb.common.util.IOUtil;
import com.fzb.common.util.http.HttpUtil;
import com.fzb.common.util.http.handle.HttpJsonArrayHandle;
import com.jfinal.kit.PathKit;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class UploadController extends BaseController {

    private static final Logger LOGGER = Logger.getLogger(UploadController.class);

    public UploadFileResponse index() {
        String uploadFieldName = "imgFile";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String fileExt = getFile().getFileName().substring(
                getFile(uploadFieldName).getFileName().lastIndexOf(".") + 1)
                .toLowerCase();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String uri = "/attached/" + getPara("dir") + "/"
                + sdf.format(new Date()) + "/" + df.format(new Date()) + "_"
                + new Random().nextInt(1000) + "." + fileExt;
        String finalFilePath = PathKit.getWebRootPath() + uri;

        IOUtil.moveOrCopyFile(PathKit.getWebRootPath() + "/attached/" + getFile(uploadFieldName).getFileName(), finalFilePath, true);
        UploadFileResponse uploadFileResponse = new UploadFileResponse();
        uploadFileResponse.setError(0);
        uploadFileResponse.setUrl(getCloudUrl(uri, finalFilePath));
        return uploadFileResponse;
    }

    private String getCloudUrl(String uri, String finalFilePath) {
        // try push to cloud
        Map<String, String[]> map = new HashMap<String, String[]>();
        map.put("fileInfo", new String[]{finalFilePath + "," + uri});
        map.put("name", new String[]{"uploadService"});
        String url;
        try {
            List<Map> urls = HttpUtil.getInstance().sendGetRequest(ZrlogUtil.getPluginServer() + "/service", map
                    , new HttpJsonArrayHandle<Map>(), ZrlogUtil.genHeaderMapByRequest(getRequest())).getT();
            if (urls != null && !urls.isEmpty()) {
                url = (String) urls.get(0).get("url");
            } else {
                url = getRequest().getContextPath() + uri;
            }
        } catch (IOException e) {
            url = getRequest().getContextPath() + uri;
            LOGGER.error(e);
        }
        return url;
    }
}
