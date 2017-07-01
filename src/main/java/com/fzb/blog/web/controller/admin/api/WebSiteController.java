package com.fzb.blog.web.controller.admin.api;

import com.fzb.blog.common.response.WebSiteSettingUpdateResponse;
import com.fzb.blog.model.WebSite;
import com.fzb.blog.service.CacheService;
import com.fzb.blog.web.controller.BaseController;

import java.util.Map;
import java.util.Map.Entry;

public class WebSiteController extends BaseController {

    private CacheService cacheService = new CacheService();

    public WebSiteSettingUpdateResponse update() {
        Map<String, String[]> tmpParamMap = getParaMap();
        for (Entry<String, String[]> param : tmpParamMap.entrySet()) {
            new WebSite().updateByKV(param.getKey(), param.getValue()[0]);
        }

        WebSiteSettingUpdateResponse updateResponse = new WebSiteSettingUpdateResponse();
        updateResponse.setError(0);
        cacheService.refreshInitDataCache(this, true);
        cacheService.removeCachedStaticFile();
        return updateResponse;
    }
}
