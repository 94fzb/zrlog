package com.zrlog.admin.web.controller.api;

import com.hibegin.common.util.BeanUtil;
import com.hibegin.common.util.FileUtils;
import com.hibegin.common.util.StringUtils;
import com.hibegin.http.annotation.ResponseBody;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.hibegin.http.server.util.PathUtil;
import com.hibegin.http.server.web.Controller;
import com.hibegin.http.server.web.cookie.Cookie;
import com.zrlog.admin.business.exception.ArgsException;
import com.zrlog.admin.business.exception.BadTemplatePathException;
import com.zrlog.admin.business.exception.TemplatePathNotNullException;
import com.zrlog.admin.business.rest.response.TemplateDownloadResponse;
import com.zrlog.admin.business.rest.response.UpdateRecordResponse;
import com.zrlog.admin.business.rest.response.UploadTemplateResponse;
import com.zrlog.admin.business.service.TemplateService;
import com.zrlog.admin.web.annotation.RefreshCache;
import com.zrlog.admin.web.token.AdminTokenThreadLocal;
import com.zrlog.business.service.TemplateHelper;
import com.zrlog.common.Constants;
import com.zrlog.common.rest.response.ApiStandardResponse;
import com.zrlog.common.vo.TemplateVO;
import com.zrlog.model.WebSite;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.I18nUtil;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TemplateController extends Controller {

    private final TemplateService templateService = new TemplateService();

    @RefreshCache
    @ResponseBody
    public ApiStandardResponse<Void> apply() throws SQLException {
        String template = Constants.TEMPLATE_BASE_PATH + request.getParaToStr("shortTemplate");
        new WebSite().updateByKV("template", template);
        ApiStandardResponse<Void> apiStandardResponse = new ApiStandardResponse<>();
        apiStandardResponse.setError(0);
        Cookie cookie = new Cookie();
        cookie.setName("template");
        cookie.setValue("");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        getResponse().addCookie(cookie);
        apiStandardResponse.setMessage(I18nUtil.getBackendStringFromRes("updateSuccess"));
        return apiStandardResponse;
    }

    @RefreshCache
    @ResponseBody
    public ApiStandardResponse<Void> preview() {
        String template = Constants.TEMPLATE_BASE_PATH + request.getParaToStr("shortTemplate");
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
        String template = checkByWhiteList(Constants.TEMPLATE_BASE_PATH + request.getParaToStr("shortTemplate"));
        File file = new File(PathUtil.getStaticPath() + template);
        if (file.exists()) {
            FileUtils.deleteFile(file.toString());
        }
        ApiStandardResponse<Void> apiStandardResponse = new ApiStandardResponse<>();
        apiStandardResponse.setMessage(I18nUtil.getBackendStringFromRes("updateSuccess"));
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

    @RefreshCache
    @ResponseBody
    public UploadTemplateResponse upload() throws IOException {
        String uploadFieldName = "file";
        File templateName = request.getFile(uploadFieldName);
        if (Objects.isNull(templateName)) {
            throw new ArgsException("file");
        }
        return templateService.upload("", templateName);
    }

    @RefreshCache
    @ResponseBody
    public UpdateRecordResponse config() throws SQLException {
        Map<String, Object> param = BeanUtil.convert(getRequest().getInputStream(), Map.class);
        if (Objects.isNull(param)) {
            return new UpdateRecordResponse(false);
        }
        String template = (String) param.get("template");
        if (StringUtils.isNotEmpty(template)) {
            param.remove("template");
            return templateService.save(template, param);
        }
        return new UpdateRecordResponse();
    }

    @ResponseBody
    public ApiStandardResponse<TemplateVO> configParams() {
        String template = Constants.TEMPLATE_BASE_PATH + request.getParaToStr("shortTemplate");
        if (StringUtils.isEmpty(template)) {
            return new ApiStandardResponse<>();
        }
        return new ApiStandardResponse<>(templateService.loadTemplateConfig(template));
    }

    @ResponseBody
    public ApiStandardResponse<List<TemplateVO>> index() {
        return new ApiStandardResponse<>(templateService.getAllTemplates(TemplateHelper.getTemplatePathByCookie(getRequest().getCookies())));
    }

    @ResponseBody
    public ApiStandardResponse<TemplateDownloadResponse> templateCenter() {
        String host = request.getParaToStr("host");
        if (StringUtils.isEmpty(host)) {
            String referer = request.getHeader("referer");
            if (StringUtils.isNotEmpty(referer)) {
                host = URI.create(referer).getAuthority();
            } else {
                host = getRequest().getHeader("Host");
            }
        }
        TemplateDownloadResponse downloadResponse = new TemplateDownloadResponse("https://store.zrlog.com/template/index.html?from=" + AdminTokenThreadLocal.getUserProtocol() + "://" + host + "/admin/template&v=" + BlogBuildInfoUtil.getVersion() + "&id=" + BlogBuildInfoUtil.getBuildId() + "&upgrade-v3=true");
        return new ApiStandardResponse<>(downloadResponse);
    }
}
