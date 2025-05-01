package com.zrlog.blog.web.controller.api;

import com.hibegin.common.util.ObjectHelpers;
import com.hibegin.http.annotation.ResponseBody;
import com.hibegin.http.server.web.Controller;
import com.zrlog.business.rest.response.PublicInfoVO;
import com.zrlog.business.service.CommonService;
import com.zrlog.blog.business.rest.response.ApiStandardResponse;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.I18nUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.zrlog.util.CrossUtils.cross;

public class BlogApiPublicController extends Controller {

    private final CommonService commonService;

    public BlogApiPublicController() {
        this.commonService = new CommonService();
    }

    @ResponseBody
    public ApiStandardResponse<Map<String, Object>> blogResource() {
        return new ApiStandardResponse<>(_blogResourceInfo());
    }

    @ResponseBody
    public ApiStandardResponse<Map<String, Object>> version() {
        cross(request, response);
        if (Objects.equals(BlogBuildInfoUtil.getBuildId(), request.getParaToStr("buildId", ""))) {
            Map<String, Object> version = _version();
            version.put("message", I18nUtil.getBackendStringFromRes("upgradeSuccess"));
            return new ApiStandardResponse<>(version);
        }
        return new ApiStandardResponse<>(new HashMap<>());
    }

    private Map<String, Object> _blogResourceInfo() {
        if (Objects.isNull(I18nUtil.threadLocal.get())) {
            return new HashMap<>();
        }
        Map<String, Object> stringObjectMap = ObjectHelpers.requireNonNullElse(I18nUtil.getBlog().get(I18nUtil.getCurrentLocale()), new HashMap<>());
        PublicInfoVO publicInfoVO = commonService.getPublicInfo(request);
        stringObjectMap.put("websiteTitle", publicInfoVO.getWebsiteTitle());
        stringObjectMap.put("homeUrl", publicInfoVO.getHomeUrl());
        stringObjectMap.put("articleRoute", "");
        stringObjectMap.put("admin_darkMode", publicInfoVO.getAdmin_darkMode());
        stringObjectMap.put("buildId", BlogBuildInfoUtil.getBuildId());
        return stringObjectMap;
    }

    private Map<String, Object> _version() {
        Map<String, Object> stringObjectMap = new HashMap<>();
        stringObjectMap.put("buildId", BlogBuildInfoUtil.getBuildId());
        return stringObjectMap;
    }

}
