package com.zrlog.admin.business.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hibegin.common.util.FileUtils;
import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.StringUtils;
import com.hibegin.common.util.ZipUtil;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.admin.business.rest.response.UpdateRecordResponse;
import com.zrlog.admin.business.rest.response.UploadTemplateResponse;
import com.zrlog.common.Constants;
import com.zrlog.common.vo.TemplateVO;
import com.zrlog.model.WebSite;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.ZrLogUtil;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

public class TemplateService {

    public static final String ADMIN_PREVIEW_IMAGE_URI = Constants.ADMIN_URI_BASE_PATH + "/template/preview-image";

    public UpdateRecordResponse save(String template, Map<String, Object> settingMap) throws SQLException {
        new WebSite().updateByKV(template + Constants.TEMPLATE_CONFIG_SUFFIX,
                new GsonBuilder().serializeNulls().create().toJson(settingMap));
        UpdateRecordResponse updateRecordResponse = new UpdateRecordResponse();
        updateRecordResponse.setMessage(I18nUtil.getBackendStringFromRes("templateUpdateSuccess"));
        return updateRecordResponse;
    }

    public UploadTemplateResponse upload(String templateName, File file) throws IOException {
        String finalPath = PathUtil.getStaticPath() + Constants.TEMPLATE_BASE_PATH;
        String finalFile = finalPath + templateName;
        FileUtils.deleteFile(finalFile);
        //start extract template file
        FileUtils.moveOrCopyFile(file.toString(), finalFile, true);
        UploadTemplateResponse response = new UploadTemplateResponse();
        response.setMessage(I18nUtil.getBackendStringFromRes("templateUploadSuccess"));
        String extractFolder = finalPath + templateName.replace(".zip", "");
        FileUtils.deleteFile(extractFolder);
        ZipUtil.unZip(finalFile, extractFolder);
        return response;
    }

    public List<TemplateVO> getAllTemplates(String previewTemplate) {
        File[] templatesFile = new File(PathUtil.getStaticPath() + Constants.TEMPLATE_BASE_PATH).listFiles();
        List<TemplateVO> templates = new ArrayList<>();
        if (templatesFile != null) {
            for (File file : templatesFile) {
                if (file.isDirectory() && !file.isHidden()) {
                    TemplateVO templateVO = getTemplateVO(file);
                    templates.add(templateVO);
                }
            }
        }

        List<TemplateVO> sortTemplates = new ArrayList<>();
        for (TemplateVO templateVO : templates) {
            if (templateVO.getTemplate().startsWith(Constants.DEFAULT_TEMPLATE_PATH)) {
                templateVO.setDeleteAble(false);
                sortTemplates.add(templateVO);
            } else {
                templateVO.setDeleteAble(true);
            }

            //同时存在以使用为主
            if (templateVO.getTemplate().equals(Constants.WEB_SITE.get("template"))) {
                templateVO.setUse(true);
                continue;
            }

            if (templateVO.getTemplate().equals(previewTemplate)) {
                templateVO.setPreview(true);
            }
        }
        for (TemplateVO templateVO : templates) {
            if (!templateVO.getTemplate().startsWith(Constants.DEFAULT_TEMPLATE_PATH)) {
                sortTemplates.add(templateVO);
            }
        }
        return sortTemplates;
    }

    private static TemplateVO getTemplateVO(File file) {
        String templatePath = file.toString().substring(PathUtil.getStaticPath().length() - 1).replace("\\", "/");
        TemplateVO templateVO = new TemplateVO();
        templateVO.setTemplate(templatePath);
        File templateInfo = new File(file + "/template.properties");
        if (templateInfo.exists()) {
            Properties properties = new Properties();
            try (InputStream in = new FileInputStream(templateInfo)) {
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
                                adminPreviewImageUrl = ADMIN_PREVIEW_IMAGE_URI + "?templateName=" + templateVO.getTemplate();
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
            } catch (IOException e) {
                //LOGGER.log(Level.SEVERE,"", e);
            }
        } else {
            templateVO.setAuthor("");
            templateVO.setName(templatePath.substring(Constants.TEMPLATE_BASE_PATH.length()));
            templateVO.setUrl("");
            templateVO.setViewType("jsp");
            templateVO.setVersion("");
        }
        if (templateVO.getPreviewImages() == null || templateVO.getPreviewImages().isEmpty()) {
            templateVO.setPreviewImages(Collections.singletonList("assets/images/template-default-preview.jpg"));
        }
        if (StringUtils.isEmpty(templateVO.getDigest())) {
            templateVO.setDigest(I18nUtil.getBlogStringFromRes("noIntroduction"));
        }
        File settingFile =
                new File(PathUtil.getStaticPath() + templatePath + "/setting/index" + ZrLogUtil.getViewExt(templateVO.getViewType()));
        templateVO.setConfigAble(settingFile.exists());
        return templateVO;
    }

    public TemplateVO loadTemplateConfig(String templateName) {
        TemplateVO templateVO = getTemplateVO(
                new File(PathUtil.getStaticPath() + templateName));
        File configFile = new File(PathUtil.getStaticPath() + templateName + "/setting/config-form.json");
        TemplateVO.TemplateConfigMap config;
        //文件存在才配置
        if (configFile.exists()) {
            String jsonStr;
            try {
                jsonStr = IOUtil.getStringInputStream(new FileInputStream(configFile));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            config = new Gson().fromJson(jsonStr, TemplateVO.TemplateConfigMap.class);
        } else {
            config = new TemplateVO.TemplateConfigMap();
        }
        String dbJsonStr = new WebSite().getStringValueByName(templateName + Constants.TEMPLATE_CONFIG_SUFFIX);
        if (StringUtils.isNotEmpty(dbJsonStr)) {
            Map<String, Object> dbConfig = new Gson().fromJson(dbJsonStr, Map.class);
            config.forEach((key, value) -> value.setValue(dbConfig.get(key)));
        }
        templateVO.setConfig(config);
        //添加一个隐藏的表单域
        TemplateVO.TemplateConfigVO templateConfigVO = new TemplateVO.TemplateConfigVO();
        templateConfigVO.setHtmlElementType("input");
        templateConfigVO.setType("hidden");
        templateConfigVO.setValue(templateName);
        config.put("template", templateConfigVO);
        return templateVO;
    }
}
