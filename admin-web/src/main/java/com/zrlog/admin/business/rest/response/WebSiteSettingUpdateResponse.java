package com.zrlog.admin.business.rest.response;

import com.zrlog.common.rest.response.StandardResponse;
import com.zrlog.util.I18nUtil;

public class WebSiteSettingUpdateResponse extends StandardResponse {

    @Override
    public String getMessage() {
        return I18nUtil.getBlogStringFromRes("updateSuccess");
    }
}
