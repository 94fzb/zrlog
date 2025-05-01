package com.zrlog.blog.web.plugin;

import com.hibegin.common.BaseLockObject;
import com.hibegin.common.util.LoggerUtil;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.blog.web.interceptor.TemplateUtils;
import com.zrlog.business.util.TemplateDownloadUtils;
import com.zrlog.common.Constants;
import com.zrlog.plugin.IPlugin;
import com.zrlog.util.BlogBuildInfoUtil;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.logging.Logger;

public class TemplateDownloadPlugin extends BaseLockObject implements IPlugin {

    private static final Logger LOGGER = LoggerUtil.getLogger(TemplateDownloadPlugin.class);

    private boolean started;

    @Override
    public boolean start() {
        if (started) {
            return true;
        }
        started = true;
        downloadTemplate(null);
        return true;
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    public void downloadTemplate(HttpRequest httpRequest) {
        String templatePath = TemplateUtils.getTemplatePath(httpRequest);
        if (Objects.equals(templatePath, Constants.DEFAULT_TEMPLATE_PATH)) {
            return;
        }
        lock.lock();
        try {
            File file = PathUtil.getStaticFile(templatePath);
            if (file.exists()) {
                return;
            }
            TemplateDownloadUtils.installByUrl(BlogBuildInfoUtil.getResourceDownloadUrl() + "/attachment/template/" + file.getName() + ".zip");
            LOGGER.info("Download lost template [" + file.getName() + "] success");
        } catch (IOException | URISyntaxException | InterruptedException e) {
            LOGGER.warning("Reinstall template error -> " + e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean stop() {
        started = false;
        return true;
    }
}
