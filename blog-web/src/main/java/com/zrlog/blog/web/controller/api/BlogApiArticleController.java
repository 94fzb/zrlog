package com.zrlog.blog.web.controller.api;

import com.hibegin.http.annotation.ResponseBody;
import com.hibegin.http.server.web.Controller;
import com.zrlog.blog.web.util.ControllerUtil;
import com.zrlog.business.rest.response.ArticleResponseEntry;
import com.zrlog.business.service.ArticleService;
import com.zrlog.business.service.VisitorArticleService;
import com.zrlog.common.rest.response.ApiStandardResponse;
import com.zrlog.data.dto.PageData;
import com.zrlog.model.Log;

import java.sql.SQLException;
import java.util.Map;

public class BlogApiArticleController extends Controller {

    private final ArticleService articleService = new ArticleService();

    @ResponseBody
    public ApiStandardResponse<Map<String, Object>> detail() throws SQLException {
        Map<String, Object> log = articleService.detail(request.getParaToStr("id"));
        return new ApiStandardResponse<>(log);
    }

    @ResponseBody
    public ApiStandardResponse<PageData<ArticleResponseEntry>> index() {
        String key = getRequest().getParaToStr("key");
        PageData<Map<String, Object>> data = new Log().visitorFind(ControllerUtil.getPageRequest(this), key);
        PageData<ArticleResponseEntry> articleResponseEntryPageData = VisitorArticleService.convertPageable(data, request);
        articleResponseEntryPageData.setKey(key);
        return new ApiStandardResponse<>(articleResponseEntryPageData);
    }
}
