package com.zrlog.business.service;

import com.hibegin.common.util.LoggerUtil;
import com.hibegin.common.util.StringUtils;
import com.zrlog.business.util.StaticFileCacheUtils;
import com.zrlog.common.Constants;
import com.zrlog.common.vo.TemplateVO;
import com.zrlog.util.I18nUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Level;

public class TemplateInfoHelper {

    public static final String ADMIN_PREVIEW_IMAGE_URI = Constants.ADMIN_URI_BASE_PATH + "/template/preview-image";

    public static TemplateVO getDefaultTemplateVO() {
        return TemplateInfoHelper.getTemplateVOByInputStream(Constants.DEFAULT_TEMPLATE_PATH, TemplateInfoHelper.class.getResourceAsStream(Constants.DEFAULT_TEMPLATE_PATH + "/template.properties"));
    }

    public static TemplateVO getTemplateVOByInputStream(String templatePath, InputStream inputStream) {
        if (Objects.isNull(inputStream)) {
            return null;
        }
        try (InputStream in = new BufferedInputStream(inputStream)) {
            TemplateVO templateVO = new TemplateVO();
            templateVO.setTemplate(templatePath);
            templateVO.setShortTemplate(new File(templatePath).getName());
            Properties properties = new Properties();
            properties.load(in);
            templateVO.setAuthor(properties.getProperty("author"));
            templateVO.setName(properties.getProperty("name"));
            templateVO.setDigest(properties.getProperty("digest"));
            templateVO.setVersion(properties.getProperty("version"));
            templateVO.setUrl(properties.getProperty("url"));
            templateVO.setViewType(properties.getProperty("viewType"));
            if (properties.get("previewImages") != null) {
                String[] images = properties.get("previewImages").toString().split(",");
                String adminPreviewImageUrl = "";
                for (int i = 0; i < images.length; i++) {
                    String image = images[i];
                    if (!image.startsWith("https://") && !image.startsWith("http://")) {
                        images[i] = templatePath + "/" + image;
                        if (i == 0) {
                            adminPreviewImageUrl = ADMIN_PREVIEW_IMAGE_URI + "?shortTemplate=" + templateVO.getShortTemplate() + "&t=" + StaticFileCacheUtils.getInstance().getFileFlagFirstByCache(images[i]);
                        }
                    } else {
                        if (i == 0) {
                            adminPreviewImageUrl = images[i];
                        }
                    }
                }
                if (images.length > 0) {
                    templateVO.setPreviewImage(images[0]);
                }
                templateVO.setAdminPreviewImage(adminPreviewImageUrl);
                templateVO.setPreviewImages(Arrays.asList(images));
            }
            if (templateVO.getPreviewImages() == null || templateVO.getPreviewImages().isEmpty()) {
                templateVO.setPreviewImages(Collections.singletonList("assets/images/template-default-preview.jpg"));
            }
            if (StringUtils.isEmpty(templateVO.getDigest())) {
                templateVO.setDigest(I18nUtil.getBlogStringFromRes("noIntroduction"));
            }
            String staticResource = properties.getProperty("staticResource");
            if (Objects.nonNull(staticResource)) {
                templateVO.setStaticResources(List.of(staticResource.split(",")));
            }
            return templateVO;
        } catch (IOException e) {
            LoggerUtil.getLogger(TemplateInfoHelper.class).log(Level.SEVERE, "", e);
            return null;
        }
    }
}
