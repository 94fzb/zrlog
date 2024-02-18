package com.zrlog.admin.web.controller.api;

import com.hibegin.common.util.FileUtils;
import com.hibegin.common.util.StringUtils;
import com.hibegin.http.annotation.ResponseBody;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.hibegin.http.server.util.PathUtil;
import com.hibegin.http.server.web.Controller;
import com.hibegin.http.server.web.cookie.Cookie;
import com.zrlog.admin.business.exception.BadTemplatePathException;
import com.zrlog.admin.business.exception.TemplatePathNotNullException;
import com.zrlog.admin.business.rest.response.TemplateDownloadResponse;
import com.zrlog.admin.business.rest.response.UpdateRecordResponse;
import com.zrlog.admin.business.rest.response.UploadTemplateResponse;
import com.zrlog.admin.business.service.TemplateService;
import com.zrlog.admin.web.annotation.RefreshCache;
import com.zrlog.blog.web.util.WebTools;
import com.zrlog.business.service.TemplateHelper;
import com.zrlog.common.Constants;
import com.zrlog.common.rest.response.ApiStandardResponse;
import com.zrlog.common.rest.response.StandardResponse;
import com.zrlog.common.vo.TemplateVO;
import com.zrlog.model.WebSite;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.RenderUtils;
import com.zrlog.util.ZrLogUtil;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TemplateController extends Controller {

    private final TemplateService templateService = new TemplateService();

    public TemplateController() {
    }

    public TemplateController(HttpRequest request, HttpResponse response) {
        super(request, response);
    }

    @RefreshCache
    @ResponseBody
    public ApiStandardResponse<Void> apply() throws SQLException {
        String template = request.getParaToStr("template");
        new WebSite().updateByKV("template", template);
        ApiStandardResponse<Void> apiStandardResponse = new ApiStandardResponse<>();
        apiStandardResponse.setError(0);
        Cookie cookie = new Cookie();
        cookie.setName("template");
        cookie.setValue("");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        getResponse().addCookie(cookie);
        apiStandardResponse.setMessage(I18nUtil.getBlogStringFromRes("updateSuccess"));
        return apiStandardResponse;
    }

    @RefreshCache
    @ResponseBody
    public ApiStandardResponse<Void> preview() {
        String template = request.getParaToStr("template");
        if (StringUtils.isEmpty(template)) {
            throw new TemplatePathNotNullException();
        }
        Cookie cookie = new Cookie();
        cookie.setName("template");
        cookie.setValue(template);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        getResponse().addCookie(cookie);
        return new ApiStandardResponse<>();
    }

    @ResponseBody
    public ApiStandardResponse<Void> delete() {
        String template = checkByWhiteList(request.getParaToStr("template"));
        File file = new File(PathUtil.getStaticPath() + template);
        if (file.exists()) {
            FileUtils.deleteFile(file.toString());
        }
        ApiStandardResponse<Void> apiStandardResponse = new ApiStandardResponse<>();
        apiStandardResponse.setMessage(I18nUtil.getBlogStringFromRes("updateSuccess"));
        return apiStandardResponse;
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
        File templateName = request.getFile(uploadFieldName);
        return templateService.upload("", templateName);
    }

    @RefreshCache
    @ResponseBody
    public UpdateRecordResponse config() throws SQLException {
        Map<String, Object> param = ZrLogUtil.convertRequestBody(getRequest(), Map.class);
        String template = (String) param.get("template");
        if (StringUtils.isNotEmpty(template)) {
            param.remove("template");
            return templateService.save(template, param);
        }
        return new UpdateRecordResponse();
    }

    @ResponseBody
    public StandardResponse configParams() {
        return RenderUtils.tryWrapperToStandardResponse(templateService.loadTemplateConfig(request.getParaToStr("template")));
    }

    @ResponseBody
    public ApiStandardResponse<List<TemplateVO>> index() {
        return new ApiStandardResponse<>(templateService.getAllTemplates(TemplateHelper.getTemplatePathByCookie(getRequest().getCookies())));
    }

    @ResponseBody
    public StandardResponse downloadUrl() {
        TemplateDownloadResponse downloadResponse = new TemplateDownloadResponse();
        downloadResponse.setUrl("https://store.zrlog.com/template/?from=http:" + WebTools.getHomeUrlWithHost(getRequest()) +
                "admin/template&v=" + BlogBuildInfoUtil.getVersion() +
                "&id=" + BlogBuildInfoUtil.getBuildId());
        return RenderUtils.tryWrapperToStandardResponse(downloadResponse);
    }
}
