package com.zrlog.business.service;

import com.hibegin.http.server.api.HttpRequest;
import com.zrlog.business.rest.response.PublicInfoVO;
import com.zrlog.common.Constants;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.ZrLogUtil;

import java.util.Objects;

public class CommonService {

    public PublicInfoVO getPublicInfo(HttpRequest request) {
        Boolean darkMode = Constants.getBooleanByFromWebSite("admin_darkMode");
        String themeColor;
        String adminColor = Constants.getStringByFromWebSite("admin_color_primary", "#1677ff");
        if (darkMode) {
            themeColor = "#000000";
        } else {
            themeColor = adminColor;
        }
        return new PublicInfoVO(BlogBuildInfoUtil.getVersion(), Constants.getStringByFromWebSite("title"), ZrLogUtil.getHomeUrlWithHost(request), darkMode, adminColor, themeColor);
    }


}
