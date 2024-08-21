package com.zrlog.blog.web.controller.api;

import com.hibegin.http.annotation.ResponseBody;
import com.hibegin.http.server.web.Controller;
import com.zrlog.business.cache.vo.BaseDataInitVO;
import com.zrlog.common.Constants;
import com.zrlog.common.rest.response.ApiStandardResponse;

public class BlogApiCacheController extends Controller {

    @ResponseBody
    public ApiStandardResponse<BaseDataInitVO> index() {
        return new ApiStandardResponse<>(Constants.zrLogConfig.getCacheService().refreshInitDataCacheAsync(request, false).join());
    }
}
