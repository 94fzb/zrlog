package com.zrlog.web.controller.admin.api;

import com.zrlog.common.response.WebSiteSettingUpdateResponse;
import com.zrlog.model.WebSite;
import com.zrlog.service.CacheService;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.ZrLogUtil;
import com.zrlog.web.controller.BaseController;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class WebSiteController extends BaseController {

    private CacheService cacheService = new CacheService();

    public WebSiteSettingUpdateResponse update() {
        Map<String, Object> requestMap = ZrLogUtil.convertRequestBody(getRequest(), Map.class);
        for (Entry<String, Object> param : requestMap.entrySet()) {
            new WebSite().updateByKV(param.getKey(), param.getValue());
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
