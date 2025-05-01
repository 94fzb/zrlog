package com.zrlog.blog.web.controller.api;

import com.hibegin.http.annotation.ResponseBody;
import com.hibegin.http.server.web.Controller;
import com.zrlog.admin.cross.CrossUtils;
import com.zrlog.business.service.CommonService;
import com.zrlog.common.rest.response.ApiStandardResponse;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.I18nUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BlogApiPublicController extends Controller {

    @ResponseBody
    public ApiStandardResponse<Map<String, Object>> blogResource() {
        return new ApiStandardResponse<>(new CommonService().blogResourceInfo(request));
    }

    private void cross() {
        if (CrossUtils.isEnableOrigin(request)) {
            //可以跨域请求
            response.addHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
            response.addHeader("Access-Control-Allow-Credentials", "true");
        }
    }

    @ResponseBody
    public ApiStandardResponse<Map<String, Object>> version() {
        cross();
        if (Objects.equals(BlogBuildInfoUtil.getBuildId(), request.getParaToStr("buildId", ""))) {
            Map<String, Object> version = new CommonService().version();
            version.put("message", I18nUtil.getBackendStringFromRes("upgradeSuccess"));
            return new ApiStandardResponse<>(version);
        }
        return new ApiStandardResponse<>(new HashMap<>());
    }

    @ResponseBody
    public ApiStandardResponse<Map<String, Object>> adminResource() {
        cross();
        return new ApiStandardResponse<>(new CommonService().adminResourceInfo(request));
    }

    @ResponseBody
    public ApiStandardResponse<Map<String, Object>> installResource() {
        return new ApiStandardResponse<>(new CommonService().installResourceInfo(getRequest()));
    }
}
