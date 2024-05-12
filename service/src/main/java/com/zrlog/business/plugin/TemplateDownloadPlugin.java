package com.zrlog.business.plugin;

import com.hibegin.common.BaseLockObject;
import com.hibegin.common.util.LoggerUtil;
import com.hibegin.common.util.ZipUtil;
import com.hibegin.common.util.http.HttpUtil;
import com.hibegin.common.util.http.handle.HttpFileHandle;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.business.exception.TemplateExistsException;
import com.zrlog.business.service.TemplateHelper;
import com.zrlog.common.Constants;
import com.zrlog.plugin.IPlugin;
import com.zrlog.util.BlogBuildInfoUtil;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.logging.Logger;

public class TemplateDownloadPlugin extends BaseLockObject implements IPlugin {

    private static final Logger LOGGER = LoggerUtil.getLogger(TemplateDownloadPlugin.class);

    @Override
    public boolean start() {
        lock.lock();
        try {
            File file = PathUtil.getStaticFile(TemplateHelper.getTemplatePath(null));
            if (file.exists()) {
                return true;
            }
            try {
                installByUrl(BlogBuildInfoUtil.getResourceDownloadUrl() + "/attachment/template/" + file.getName() + ".zip");
                LOGGER.info("Download lost " + file.getName() + " template success");
            } catch (IOException | URISyntaxException | InterruptedException e) {
                LOGGER.warning("Reinstall template error -> " + e.getMessage());
            }
        } finally {
            lock.unlock();
        }
        return true;
    }

    @Override
    public boolean stop() {
        return false;
    }

    public static void installByUrl(String downloadUrl) throws IOException, URISyntaxException, InterruptedException {
        String templateName = downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1).replace(".zip", "");
        File path = new File(PathUtil.getStaticPath() + Constants.TEMPLATE_BASE_PATH + templateName);
        if (path.exists()) {
            throw new TemplateExistsException();
        }
        HttpFileHandle fileHandle = (HttpFileHandle) HttpUtil.getInstance().sendGetRequest(downloadUrl, new HttpFileHandle(PathUtil.getStaticPath() + Constants.TEMPLATE_BASE_PATH), new HashMap<>());
        ZipUtil.unZip(fileHandle.getT().toString(), path.toString());
        //delete zip file
        fileHandle.getT().delete();
    }
}
