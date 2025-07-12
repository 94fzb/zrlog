package com.zrlog.blog.web.interceptor;

import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.util.FreeMarkerUtil;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.business.service.TemplateHelper;
import com.zrlog.common.Constants;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

public class TemplateUtils {

    /**
     * 获取主题的相对于程序的路径，当Cookie中有值的情况下，优先使用Cookie里面的数据（仅当主题存在的情况下，否则返回默认的主题），
     */
    public static String getTemplatePath(HttpRequest request) {
        String templatePath = Constants.getStringByFromWebSite("template", Constants.DEFAULT_TEMPLATE_PATH);
        if (Objects.isNull(request)) {
            return templatePath;
        }
        String previewTheme = TemplateHelper.getTemplatePathByCookie(request.getCookies());
        if (previewTheme != null) {
            if (new File(PathUtil.getStaticPath() + templatePath).exists()) {
                return previewTheme;
            }
            return Constants.DEFAULT_TEMPLATE_PATH;
        }
        return templatePath;
    }

    public static void initTemplate() {
        if (!Constants.zrLogConfig.isInstalled()) {
            return;
        }
        String configTemplate = Constants.getStringByFromWebSite("template", Constants.DEFAULT_TEMPLATE_PATH);
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
        String configTemplate = Constants.getStringByFromWebSite("template", Constants.DEFAULT_TEMPLATE_PATH);
        File path = new File(PathUtil.getStaticPath() + configTemplate);
        if (path.exists() && !Objects.equals(configTemplate, Constants.DEFAULT_TEMPLATE_PATH)) {
            return Arrays.stream(Objects.requireNonNull(path.listFiles())).anyMatch(e -> e.getName().startsWith(templateName + "."));
        } else {
            return Objects.nonNull(TemplateUtils.class.getResourceAsStream(Constants.DEFAULT_TEMPLATE_PATH + "/" + templateName + ".ftl"));
        }
    }
}
