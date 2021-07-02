package com.zrlog.admin.business.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hibegin.common.util.FileUtils;
import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.StringUtils;
import com.hibegin.common.util.ZipUtil;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.zrlog.admin.business.rest.response.UpdateRecordResponse;
import com.zrlog.admin.business.rest.response.UploadTemplateResponse;
import com.zrlog.business.service.TemplateHelper;
import com.zrlog.common.Constants;
import com.zrlog.common.vo.TemplateVO;
import com.zrlog.model.WebSite;
import com.zrlog.util.I18nUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TemplateService {

    public UpdateRecordResponse save(String template, Map<String, Object> settingMap) {
        new WebSite().updateByKV(template + Constants.TEMPLATE_CONFIG_SUFFIX,
                new GsonBuilder().serializeNulls().create().toJson(settingMap));
        UpdateRecordResponse updateRecordResponse = new UpdateRecordResponse();
        updateRecordResponse.setMessage(I18nUtil.getBlogStringFromRes("templateUpdateSuccess"));
        return updateRecordResponse;
    }

    public UploadTemplateResponse upload(String templateName, File file) throws IOException {
        String finalPath = PathKit.getWebRootPath() + Constants.TEMPLATE_BASE_PATH;
        String finalFile = finalPath + templateName;
        FileUtils.deleteFile(finalFile);
        //start extract template file
        FileUtils.moveOrCopyFile(file.toString(), finalFile, true);
        UploadTemplateResponse response = new UploadTemplateResponse();
        response.setMessage(I18nUtil.getBlogStringFromRes("templateUploadSuccess"));
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
                    TemplateVO templateVO = TemplateHelper.getTemplateVO(contextPath, file);
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

    public TemplateVO loadTemplateConfig(String templateName) {
        TemplateVO templateVO = TemplateHelper.getTemplateVO(JFinal.me().getContextPath(),
                new File(PathKit.getWebRootPath() + templateName));
        File configFile = new File(PathKit.getWebRootPath() + templateName + "/setting/config-form.json");
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
