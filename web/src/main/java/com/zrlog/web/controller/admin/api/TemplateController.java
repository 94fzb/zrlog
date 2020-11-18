package com.zrlog.web.controller.admin.api;

import com.hibegin.common.util.FileUtils;
import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.StringUtils;
import com.jfinal.kit.PathKit;
import com.zrlog.common.Constants;
import com.zrlog.common.rest.response.StandardResponse;
import com.zrlog.common.vo.TemplateVO;
import com.zrlog.model.WebSite;
import com.zrlog.business.rest.response.*;
import com.zrlog.business.service.TemplateService;
import com.zrlog.util.ZrLogUtil;
import com.zrlog.web.annotation.RefreshCache;
import com.zrlog.web.controller.BaseController;
import com.zrlog.web.exception.BadTemplatePathException;
import com.zrlog.web.interceptor.TemplateHelper;

import javax.servlet.http.Cookie;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TemplateController extends BaseController {

    private final TemplateService templateService = new TemplateService();

    @RefreshCache
    public WebSiteSettingUpdateResponse apply() {
        String template = getPara("template");
        new WebSite().updateByKV("template", template);
        WebSiteSettingUpdateResponse webSiteSettingUpdateResponse = new WebSiteSettingUpdateResponse();
        webSiteSettingUpdateResponse.setError(0);
        Cookie cookie = new Cookie("template", "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        getResponse().addCookie(cookie);
        return webSiteSettingUpdateResponse;
    }

    public WebSiteSettingUpdateResponse delete() {
        String template = checkByWhiteList(getPara("template"));
        File file = new File(PathKit.getWebRootPath() + template);
        if (file.exists()) {
            FileUtils.deleteFile(file.toString());
        }
        return new WebSiteSettingUpdateResponse();
    }

    private String checkByWhiteList(String filePath) {
        if (Objects.isNull(filePath)) {
            throw new BadTemplatePathException("");
        }
        filePath = filePath.replace("\\", "/");
        if (filePath.contains("../")) {
            throw new BadTemplatePathException(filePath);
        }
        if (!filePath.startsWith("/error") && !filePath.startsWith(Constants.TEMPLATE_BASE_PATH)) {
            throw new BadTemplatePathException(filePath);
        }
        return filePath;
    }

    public UploadTemplateResponse upload() throws IOException {
        String uploadFieldName = "file";
        String templateName = getFile(uploadFieldName).getOriginalFileName();
        return templateService.upload(templateName, getFile(uploadFieldName).getFile());
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
