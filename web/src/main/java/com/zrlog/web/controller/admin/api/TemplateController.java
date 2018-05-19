package com.zrlog.web.controller.admin.api;

import com.hibegin.common.util.FileUtils;
import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.StringUtils;
import com.jfinal.kit.PathKit;
import com.zrlog.common.response.*;
import com.zrlog.model.WebSite;
import com.zrlog.service.TemplateService;
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

    private TemplateService templateService = new TemplateService();

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

    public LoadFileResponse loadFile() throws FileNotFoundException {
        LoadFileResponse loadFileResponse = new LoadFileResponse();
        loadFileResponse.setFileContent(IOUtil.getStringInputStream(new FileInputStream(PathKit.getWebRootPath() + getPara("file"))));
        return loadFileResponse;
    }

    @RefreshCache
    public StandardResponse saveFile() {
        String file = PathKit.getWebRootPath() + getPara("file");
        IOUtil.writeBytesToFile(getPara("content").getBytes(), new File(file));
        return new StandardResponse();
    }

    public UploadTemplateResponse upload() throws IOException {
        String uploadFieldName = "file";
        String templateName = getFile(uploadFieldName).getOriginalFileName();
        return templateService.upload(templateName, getFile(uploadFieldName).getFile());
    }

    public ListFileResponse files() {
        ListFileResponse listFileResponse = new ListFileResponse();
        listFileResponse.setFiles(templateService.getFiles(getPara("path")));
        return listFileResponse;
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
        return templateService.save(template, settingMap);
    }

    @RefreshCache
    public UpdateRecordResponse config() {
        Map<String, Object> param = ZrLogUtil.convertRequestBody(getRequest(), Map.class);
        String template = (String) param.get("template");
        if (StringUtils.isNotEmpty(template)) {
            param.remove("template");
            return templateService.save(template, param);
        }
        return new UpdateRecordResponse();
    }
}
