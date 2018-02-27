package com.zrlog.web.controller.admin.api;

import com.google.gson.Gson;
import com.hibegin.common.util.FileUtils;
import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.StringUtils;
import com.hibegin.common.util.ZipUtil;
import com.jfinal.kit.PathKit;
import com.zrlog.common.Constants;
import com.zrlog.common.response.UpdateRecordResponse;
import com.zrlog.common.response.UploadTemplateResponse;
import com.zrlog.common.response.WebSiteSettingUpdateResponse;
import com.zrlog.model.WebSite;
import com.zrlog.util.I18NUtil;
import com.zrlog.util.ZrLogUtil;
import com.zrlog.web.annotation.RefreshCache;
import com.zrlog.web.controller.BaseController;

import javax.servlet.http.Cookie;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TemplateController extends BaseController {

    @RefreshCache
    public WebSiteSettingUpdateResponse apply() {
        String template = getPara("template");
        new WebSite().updateByKV("template", template);
        WebSiteSettingUpdateResponse webSiteSettingUpdateResponse = new WebSiteSettingUpdateResponse();
        webSiteSettingUpdateResponse.setError(0);
        Cookie cookie = new Cookie("template", "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        getResponse().addCookie(cookie);
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

    public Map loadFile() throws FileNotFoundException {
        String file = PathKit.getWebRootPath() + getPara("file");
        Map<String, Object> map = new HashMap<>();
        String fileContent = IOUtil.getStringInputStream(new FileInputStream(file));
        map.put("fileContent", fileContent);
        return map;
    }

    public Map saveFile() {
        String file = PathKit.getWebRootPath() + getPara("file");
        IOUtil.writeBytesToFile(getPara("content").getBytes(), new File(file));
        Map<String, Object> map = new HashMap<>();
        map.put("status", 200);
        return map;
    }

    public UploadTemplateResponse upload() throws IOException {
        String uploadFieldName = "file";

        String templateName = getFile(uploadFieldName).getOriginalFileName();
        String finalPath = PathKit.getWebRootPath() + Constants.TEMPLATE_BASE_PATH;
        String finalFile = finalPath + templateName;
        FileUtils.deleteFile(finalFile);
        //start extract template file
        FileUtils.moveOrCopyFile(getFile(uploadFieldName).getFile().toString(), finalFile, true);
        UploadTemplateResponse response = new UploadTemplateResponse();
        response.setMessage(I18NUtil.getStringFromRes("templateDownloadSuccess", getRequest()));
        String extractFolder = finalPath + templateName.replace(".zip", "") + "/";
        FileUtils.deleteFile(extractFolder);
        ZipUtil.unZip(finalFile, extractFolder);
        return response;
    }

    /**
     * @return
     * @deprecated 主题设置建议直接使用config方法
     */
    @Deprecated
    @RefreshCache
    public UpdateRecordResponse setting() {
        Map<String, String[]> param = getRequest().getParameterMap();
        String template = getPara("template");
        Map<String, Object> settingMap = new HashMap<>();
        for (Map.Entry<String, String[]> entry : param.entrySet()) {
            if (!"template".equals(entry.getKey())) {
                if (entry.getValue().length > 1) {
                    settingMap.put(entry.getKey(), entry.getValue());
                } else {
                    settingMap.put(entry.getKey(), entry.getValue()[0]);
                }
            }
        }
        return save(template, settingMap);
    }

    @RefreshCache
    public UpdateRecordResponse config() {
        Map<String, Object> param = ZrLogUtil.convertRequestBody(getRequest(), Map.class);
        String template = (String) param.get("template");
        if (StringUtils.isNotEmpty(template)) {
            param.remove("template");
            return save(template, param);
        }
        return new UpdateRecordResponse();
    }

    @RefreshCache
    private UpdateRecordResponse save(String template, Map<String, Object> settingMap) {
        new WebSite().updateByKV(template + templateConfigSuffix, new Gson().toJson(settingMap));
        UpdateRecordResponse updateRecordResponse = new UpdateRecordResponse();
        updateRecordResponse.setMessage("变更成功");
        return updateRecordResponse;
    }

}
