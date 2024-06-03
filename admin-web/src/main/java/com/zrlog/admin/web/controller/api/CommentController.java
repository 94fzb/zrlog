package com.zrlog.admin.web.controller.api;

import com.hibegin.common.util.BeanUtil;
import com.hibegin.http.annotation.ResponseBody;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.hibegin.http.server.web.Controller;
import com.zrlog.admin.business.rest.request.ReadCommentRequest;
import com.zrlog.admin.business.rest.response.UpdateRecordResponse;
import com.zrlog.admin.business.service.AdminCommentService;
import com.zrlog.admin.business.util.ControllerUtil;
import com.zrlog.admin.web.annotation.RefreshCache;
import com.zrlog.common.rest.response.ApiStandardResponse;
import com.zrlog.common.rest.response.StandardResponse;
import com.zrlog.data.dto.PageData;

import java.sql.SQLException;
import java.util.Map;

public class CommentController extends Controller {

    private final AdminCommentService commentService = new AdminCommentService();

    @RefreshCache
    @ResponseBody
    public StandardResponse delete() throws SQLException {
        return commentService.delete(request.getParaToStr("id").split(","));
    }

    @ResponseBody
    public UpdateRecordResponse read() {
        return commentService.read(BeanUtil.convertWithValid(getRequest().getInputStream(), ReadCommentRequest.class));
    }

    @ResponseBody
    public ApiStandardResponse<PageData<Map<String, Object>>> index() throws SQLException {
        return new ApiStandardResponse<>(commentService.page(ControllerUtil.getPageRequest(this)));
    }
}
