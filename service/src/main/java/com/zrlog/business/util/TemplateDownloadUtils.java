package com.zrlog.business.util;

import com.hibegin.common.util.StringUtils;
import com.hibegin.common.util.ZipUtil;
import com.hibegin.common.util.http.HttpUtil;
import com.hibegin.common.util.http.handle.HttpFileHandle;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.common.Constants;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

public class TemplateDownloadUtils {

    public static void installByUrl(String downloadUrl) throws IOException, URISyntaxException, InterruptedException {
        if (StringUtils.isEmpty(downloadUrl)) {
            return;
        }
        String templateName = downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1).replace(".zip", "");
        File path = PathUtil.getStaticFile(Constants.TEMPLATE_BASE_PATH + templateName);
        HttpFileHandle fileHandle = (HttpFileHandle) HttpUtil.getInstance().sendGetRequest(downloadUrl, new HttpFileHandle(PathUtil.getStaticFile(Constants.TEMPLATE_BASE_PATH).toString()), new HashMap<>());
        if (!fileHandle.getT().exists()) {
            return;
        }
        ZipUtil.unZip(fileHandle.getT().toString(), path.toString());
        //delete zip file
        fileHandle.getT().delete();
    }
}
