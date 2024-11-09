package com.zrlog.blog.web.interceptor;

import com.hibegin.http.server.util.FreeMarkerUtil;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.business.util.InstallUtils;
import com.zrlog.common.Constants;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

public class TemplateUtils {

    public static void initTemplate() {
        if (!InstallUtils.isInstalled()) {
            return;
        }
        String configTemplate = Constants.zrLogConfig.getPublicWebSite().getOrDefault("template", Constants.DEFAULT_TEMPLATE_PATH).toString();
        File path = new File(PathUtil.getStaticPath() + configTemplate);
        if (path.exists() && !Objects.equals(configTemplate, Constants.DEFAULT_TEMPLATE_PATH)) {
            try {
                FreeMarkerUtil.init(path.getPath());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                FreeMarkerUtil.initClassTemplate(Constants.DEFAULT_TEMPLATE_PATH);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static boolean existsByTemplateName(String templateName) {
        String configTemplate = Constants.zrLogConfig.getPublicWebSite().getOrDefault("template", Constants.DEFAULT_TEMPLATE_PATH).toString();
        File path = new File(PathUtil.getStaticPath() + configTemplate);
        if (path.exists() && !Objects.equals(configTemplate, Constants.DEFAULT_TEMPLATE_PATH)) {
            return Arrays.stream(Objects.requireNonNull(path.listFiles())).anyMatch(e -> e.getName().startsWith(templateName + "."));
        } else {
            return Objects.nonNull(TemplateUtils.class.getResourceAsStream( Constants.DEFAULT_TEMPLATE_PATH + "/" + templateName + ".ftl"));
        }
    }
}
