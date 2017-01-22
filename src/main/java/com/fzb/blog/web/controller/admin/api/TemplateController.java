package com.fzb.blog.web.controller.admin.api;

import com.fzb.blog.common.response.UpdateRecordResponse;
import com.fzb.blog.common.response.UploadTemplateResponse;
import com.fzb.blog.common.response.WebSiteSettingUpdateResponse;
import com.fzb.blog.model.WebSite;
import com.fzb.blog.service.CacheService;
import com.fzb.blog.util.I18NUtil;
import com.fzb.blog.web.controller.BaseController;
import com.fzb.common.util.IOUtil;
import com.fzb.common.util.ZipUtil;
import com.jfinal.kit.PathKit;
import flexjson.JSONSerializer;

import javax.servlet.http.Cookie;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TemplateController extends BaseController {

    private CacheService cacheService = new CacheService();

    public WebSiteSettingUpdateResponse apply() {
        String template = getPara("template");
        new WebSite().updateByKV("template", template);
        WebSiteSettingUpdateResponse webSiteSettingUpdateResponse = new WebSiteSettingUpdateResponse();
        webSiteSettingUpdateResponse.setError(0);
        Cookie cookie = new Cookie("template", "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        getResponse().addCookie(cookie);
        // 更新缓存数据
        cacheService.refreshInitDataCache(this);
        return webSiteSettingUpdateResponse;
    }

    public Map loadFile() {
        String file = getRequest().getRealPath("/") + getPara("file");
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            String fileContent = IOUtil.getStringInputStream(new FileInputStream(file));
            map.put("fileContent", fileContent);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return map;
    }

    public Map saveFile() {
        String file = getRequest().getRealPath("/") + getPara("file");
        IOUtil.writeBytesToFile(getPara("content").getBytes(), new File(file));
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("status", 200);
        return map;
    }

    public UploadTemplateResponse upload() {
        String uploadFieldName = "file";

        String fileName = getFile(uploadFieldName).getFileName();
        String finalPath = PathKit.getWebRootPath() + "/include/templates/";
        String finalFile = finalPath + fileName;
        IOUtil.moveOrCopyFile(PathKit.getWebRootPath() + "/attached/" + fileName, finalFile, true);
        UploadTemplateResponse response = new UploadTemplateResponse();
        response.setMessage(I18NUtil.getStringFromRes("templateDownloadSuccess", getRequest()));
        try {
            ZipUtil.unZip(finalFile, finalPath + fileName.replace(".zip", "") + "/");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public UpdateRecordResponse setting() {
        Map<String, String[]> param = getRequest().getParameterMap();
        String template = getPara("template");
        Map<String, Object> settingMap = new HashMap<String, Object>();
        for (Map.Entry<String, String[]> entry : param.entrySet()) {
            if (!entry.getKey().equals("template")) {
                if (entry.getValue().length > 1) {
                    settingMap.put(entry.getKey(), entry.getValue());
                } else {
                    settingMap.put(entry.getKey(), entry.getValue()[0]);
                }
            }
        }
        new WebSite().updateByKV(template + templateConfigSuffix, new JSONSerializer().deepSerialize(settingMap));
        cacheService.refreshInitDataCache(this);
        UpdateRecordResponse updateRecordResponse = new UpdateRecordResponse();
        updateRecordResponse.setMessage("变更成功");
        return updateRecordResponse;
    }

}
