package com.zrlog.business.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hibegin.common.util.FileUtils;
import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.StringUtils;
import com.hibegin.common.util.ZipUtil;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.zrlog.business.rest.response.UpdateRecordResponse;
import com.zrlog.business.rest.response.UploadTemplateResponse;
import com.zrlog.common.Constants;
import com.zrlog.common.vo.TemplateVO;
import com.zrlog.model.WebSite;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.ZrLogUtil;

import javax.servlet.http.Cookie;
import java.io.*;
import java.util.*;

public class TemplateService {

    /**
     * 根据文件后缀 查找符合要求文件列表
     *
     * @param path
     * @param prefix
     */
    private static void fillFileInfo(String path, List<String> fileList, String... prefix) {
        File[] files = new File(path).listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory() && new File(file.getAbsolutePath()).listFiles() != null) {
                    fillFileInfo(file.getAbsolutePath(), fileList, prefix);
                } else {
                    for (String pre : prefix) {
                        if (file.getAbsoluteFile().toString().endsWith(pre)) {
                            fileList.add(file.getAbsoluteFile().toString());
                        }
                    }
                }
            }
        }
    }

    public UpdateRecordResponse save(String template, Map<String, Object> settingMap) {
        new WebSite().updateByKV(template + Constants.TEMPLATE_CONFIG_SUFFIX, new GsonBuilder().serializeNulls().create().toJson(settingMap));
        UpdateRecordResponse updateRecordResponse = new UpdateRecordResponse();
        updateRecordResponse.setMessage(I18nUtil.getStringFromRes("templateUpdateSuccess"));
        return updateRecordResponse;
    }

    public UploadTemplateResponse upload(String templateName, File file) throws IOException {
        String finalPath = PathKit.getWebRootPath() + Constants.TEMPLATE_BASE_PATH;
        String finalFile = finalPath + templateName;
        FileUtils.deleteFile(finalFile);
        //start extract template file
        FileUtils.moveOrCopyFile(file.toString(), finalFile, true);
        UploadTemplateResponse response = new UploadTemplateResponse();
        response.setMessage(I18nUtil.getStringFromRes("templateUploadSuccess"));
        String extractFolder = finalPath + templateName.replace(".zip", "") + "/";
        FileUtils.deleteFile(extractFolder);
        ZipUtil.unZip(finalFile, extractFolder);
        return response;
    }

    public List<TemplateVO> getAllTemplates(String contextPath, String previewTemplate) {
        File[] templatesFile = new File(PathKit.getWebRootPath() + Constants.TEMPLATE_BASE_PATH).listFiles();
        List<TemplateVO> templates = new ArrayList<>();
        if (templatesFile != null) {
            for (File file : templatesFile) {
                if (file.isDirectory() && !file.isHidden()) {
                    TemplateVO templateVO = getTemplateVO(contextPath, file);
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

    public TemplateVO getTemplateVO(String contextPath, File file) {
        String templatePath = file.toString().substring(PathKit.getWebRootPath().length()).replace("\\", "/");
        TemplateVO templateVO = new TemplateVO();
        File templateInfo = new File(file.toString() + "/template.properties");
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
                    for (int i = 0; i < images.length; i++) {
                        String image = images[i];
                        if (!image.startsWith("https://") && !image.startsWith("http://")) {
                            images[i] = contextPath + templatePath + "/" + image;
                        }
                    }
                    if (images.length > 0) {
                        templateVO.setPreviewImage(images[0]);
                    }
                    templateVO.setPreviewImages(Arrays.asList(images));
                }
            } catch (IOException e) {
                //LOGGER.error("", e);
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
            templateVO.setDigest(I18nUtil.getStringFromRes("noIntroduction"));
        }
        File settingFile = new File(PathKit.getWebRootPath() + templatePath + "/setting/index" + ZrLogUtil.getViewExt(templateVO.getViewType()));
        templateVO.setConfigAble(settingFile.exists());
        templateVO.setTemplate(templatePath);
        return templateVO;
    }

    public List<String> getFiles(String path) {
        List<String> fileList = new ArrayList<>();
        fillFileInfo(PathKit.getWebRootPath() + path, fileList, ".jsp", ".js", ".css", ".html");
        String webPath = JFinal.me().getServletContext().getRealPath("/");
        List<String> strFile = new ArrayList<>();
        for (String aFileList : fileList) {
            strFile.add(aFileList.substring(webPath.length() - 1 + path.length()).replace('\\', '/'));
        }
        //使用字典序
        return new ArrayList<>(new TreeSet<>(strFile));
    }

    public String getTemplatePathByCookie(Cookie[] cookies) {
        String previewTemplate = null;
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if ("template".equals(cookie.getName()) && cookie.getValue().startsWith(Constants.TEMPLATE_BASE_PATH)) {
                    previewTemplate = cookie.getValue();
                    break;
                }
            }
        }
        return previewTemplate;
    }

    public TemplateVO loadTemplateConfig(String templateName) {
        TemplateVO templateVO = getTemplateVO(JFinal.me().getContextPath(), new File(PathKit.getWebRootPath() + templateName));
        File configFile = new File(PathKit.getWebRootPath() + templateName + "/setting/config-form.json");
        if (!configFile.exists()) {
            return templateVO;
        }
        try {
            String jsonStr = IOUtil.getStringInputStream(new FileInputStream(configFile));
            TemplateVO.TemplateConfigMap config = new Gson().fromJson(jsonStr, TemplateVO.TemplateConfigMap.class);
            if (StringUtils.isNotEmpty(jsonStr)) {
                String dbJsonStr = new WebSite().getStringValueByName(templateName + Constants.TEMPLATE_CONFIG_SUFFIX);
                if (StringUtils.isNotEmpty(dbJsonStr)) {
                    Map<String, Object> dbConfig = new Gson().fromJson(dbJsonStr, Map.class);
                    config.forEach((key, value) -> value.setValue(dbConfig.get(key)));
                }
            }
            templateVO.setConfig(config);
            //添加一个隐藏的表单域
            TemplateVO.TemplateConfigVO templateConfigVO = new TemplateVO.TemplateConfigVO();
            templateConfigVO.setHtmlElementType("input");
            templateConfigVO.setType("hidden");
            templateConfigVO.setValue(templateName);
            config.put("template", templateConfigVO);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return templateVO;
    }
}
