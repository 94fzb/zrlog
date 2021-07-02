package com.zrlog.admin.web.controller.api;

import com.hibegin.common.util.FileUtils;
import com.hibegin.common.util.StringUtils;
import com.jfinal.core.Controller;
import com.jfinal.kit.PathKit;
import com.zrlog.admin.business.exception.BadTemplatePathException;
import com.zrlog.admin.business.exception.TemplatePathNotNullException;
import com.zrlog.admin.business.rest.response.TemplateDownloadResponse;
import com.zrlog.admin.business.rest.response.UpdateRecordResponse;
import com.zrlog.admin.business.rest.response.UploadTemplateResponse;
import com.zrlog.admin.business.rest.response.WebSiteSettingUpdateResponse;
import com.zrlog.admin.business.service.TemplateService;
import com.zrlog.admin.web.annotation.RefreshCache;
import com.zrlog.blog.web.util.WebTools;
import com.zrlog.business.service.TemplateHelper;
import com.zrlog.common.Constants;
import com.zrlog.common.rest.response.ApiStandardResponse;
import com.zrlog.common.vo.TemplateVO;
import com.zrlog.model.WebSite;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.ZrLogUtil;

import javax.servlet.http.Cookie;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TemplateController extends Controller {

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

    @RefreshCache
    public ApiStandardResponse preview() {
        String template = getPara("template");
        if (StringUtils.isEmpty(template)) {
            throw new TemplatePathNotNullException();
        }
        Cookie cookie = new Cookie("template", template);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        getResponse().addCookie(cookie);
        return new ApiStandardResponse();
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

    public TemplateVO configParams() {
        return templateService.loadTemplateConfig(get("template"));
    }

    public List<TemplateVO> index() {
        return templateService.getAllTemplates(getRequest().getContextPath(),
                TemplateHelper.getTemplatePathByCookie(getRequest().getCookies()));
    }

    public TemplateDownloadResponse downloadUrl() {
        TemplateDownloadResponse downloadResponse = new TemplateDownloadResponse();
        downloadResponse.setUrl("https://store.zrlog.com/template/?from=http:" + WebTools.getHomeUrlWithHost(getRequest()) +
                "admin/template&v=" + BlogBuildInfoUtil.getVersion() +
                "&id=" + BlogBuildInfoUtil.getBuildId());
        return downloadResponse;
    }
}
