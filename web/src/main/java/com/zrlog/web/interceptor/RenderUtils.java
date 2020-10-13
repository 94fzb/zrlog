package com.zrlog.web.interceptor;

import com.zrlog.common.rest.response.ApiStandardResponse;
import com.zrlog.common.rest.response.StandardResponse;

class RenderUtils {

    public static StandardResponse tryWrapperToStandardResponse(Object ret) {
        if (ret instanceof StandardResponse) {
            return (StandardResponse) ret;
        } else {
            ApiStandardResponse a = new ApiStandardResponse();
            a.setData(ret);
            return a;
        }
    }
}
