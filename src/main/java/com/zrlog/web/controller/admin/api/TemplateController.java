package com.zrlog.web.controller.admin.api;

import com.hibegin.common.util.FileUtils;
import com.zrlog.common.Constants;
import com.zrlog.common.response.UpdateRecordResponse;
import com.zrlog.common.response.UploadTemplateResponse;
import com.zrlog.common.response.WebSiteSettingUpdateResponse;
import com.zrlog.model.WebSite;
import com.zrlog.service.CacheService;
import com.zrlog.util.I18NUtil;
import com.zrlog.web.controller.BaseController;
import com.google.gson.Gson;
import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.ZipUtil;
import com.jfinal.kit.PathKit;

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
        cacheService.refreshInitDataCache(this, true);
        cacheService.removeCachedStaticFile();
        return webSiteSettingUpdateResponse;
    }

    public WebSiteSettingUpdateResponse delete() {
        String template = getPara("template");
        File file = new File(PathKit.getWebRootPath() + template);
        if (file.exists()) {
            FileUtils.deleteFile(file.toString());
        }
        return new WebSiteSettingUpdateResponse();
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

        String templateName = getFile(uploadFieldName).getOriginalFileName();
        String finalPath = PathKit.getWebRootPath() + Constants.TEMPLATE_BASE_PATH;
        String finalFile = finalPath + templateName;
        FileUtils.deleteFile(finalFile);
        //start extract template file
        FileUtils.moveOrCopyFile(getFile(uploadFieldName).getFile().toString(), finalFile, true);
        UploadTemplateResponse response = new UploadTemplateResponse();
        response.setMessage(I18NUtil.getStringFromRes("templateDownloadSuccess", getRequest()));
        try {
            String extractFolder = finalPath + templateName.replace(".zip", "") + "/";
            FileUtils.deleteFile(extractFolder);
            ZipUtil.unZip(finalFile, extractFolder);
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
        new WebSite().updateByKV(template + templateConfigSuffix, new Gson().toJson(settingMap));
        cacheService.refreshInitDataCache(this, true);
        cacheService.removeCachedStaticFile();
        UpdateRecordResponse updateRecordResponse = new UpdateRecordResponse();
        updateRecordResponse.setMessage("变更成功");
        return updateRecordResponse;
    }

}
