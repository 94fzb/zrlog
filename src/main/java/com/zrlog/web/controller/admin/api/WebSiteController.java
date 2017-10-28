package com.zrlog.web.controller.admin.api;

import com.zrlog.common.response.WebSiteSettingUpdateResponse;
import com.zrlog.service.CacheService;
import com.zrlog.model.WebSite;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.web.controller.BaseController;

import java.util.HashMap;
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

    public Map<String, Object> version() {
        Map<String, Object> map = new HashMap<>();
        map.put("version", BlogBuildInfoUtil.getVersion());
        map.put("buildId", BlogBuildInfoUtil.getBuildId());
        return map;
    }
}
