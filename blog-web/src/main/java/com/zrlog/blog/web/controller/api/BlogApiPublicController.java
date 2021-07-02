package com.zrlog.blog.web.controller.api;

import com.jfinal.core.Controller;
import com.zrlog.business.service.CommonService;
import com.zrlog.business.util.InstallUtils;

import java.util.HashMap;
import java.util.Map;

public class BlogApiPublicController extends Controller {

    public Map<String, Object> blogResource() {
        return new CommonService().blogResourceInfo();
    }

    public Map<String, Object> installResource() {
        return new CommonService().installResourceInfo(getRequest());
    }

    public Map<String, Boolean> installed() {
        Map<String, Boolean> result = new HashMap<>();
        result.put("installed", InstallUtils.isInstalled());
        return result;
    }
}
