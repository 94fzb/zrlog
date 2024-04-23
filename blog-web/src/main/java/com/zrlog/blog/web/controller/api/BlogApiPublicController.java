package com.zrlog.blog.web.controller.api;

import com.hibegin.http.annotation.ResponseBody;
import com.hibegin.http.server.web.Controller;
import com.zrlog.business.service.CommonService;
import com.zrlog.common.rest.response.ApiStandardResponse;

import java.util.Map;

public class BlogApiPublicController extends Controller {

    @ResponseBody
    public ApiStandardResponse<Map<String, Object>> blogResource() {
        return new ApiStandardResponse<>(new CommonService().blogResourceInfo(request));
    }

    @ResponseBody
    public ApiStandardResponse<Map<String, Object>> installResource() {
        return new ApiStandardResponse<>(new CommonService().installResourceInfo(getRequest()));
    }
}
