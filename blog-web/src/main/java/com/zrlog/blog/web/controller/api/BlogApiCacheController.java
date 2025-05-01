package com.zrlog.blog.web.controller.api;

import com.hibegin.http.annotation.ResponseBody;
import com.hibegin.http.server.web.Controller;
import com.zrlog.common.Constants;
import com.zrlog.blog.business.rest.response.ApiStandardResponse;
import com.zrlog.data.cache.vo.BaseDataInitVO;

public class BlogApiCacheController extends Controller {

    @ResponseBody
    public ApiStandardResponse<BaseDataInitVO> index() {
        return new ApiStandardResponse<>((BaseDataInitVO) Constants.zrLogConfig.getCacheService().getInitData());
    }
}
