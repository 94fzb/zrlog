package com.zrlog.admin.web.controller.api;

import com.zrlog.admin.web.annotation.RefreshCache;
import com.zrlog.blog.web.controller.BaseController;
import com.zrlog.business.rest.request.ReadCommentRequest;
import com.zrlog.business.rest.response.UpdateRecordResponse;
import com.zrlog.business.service.CommentService;
import com.zrlog.common.rest.response.StandardResponse;
import com.zrlog.data.dto.PageData;
import com.zrlog.model.Comment;
import com.zrlog.util.ZrLogUtil;

public class CommentController extends BaseController {

    private final CommentService commentService = new CommentService();

    @RefreshCache
    public StandardResponse delete() {
        return commentService.delete(getPara("id").split(","));
    }

    public UpdateRecordResponse read() {
        return commentService.read(ZrLogUtil.convertRequestBody(getRequest(), ReadCommentRequest.class));
    }

    public PageData<Comment> index() {
        return commentService.page(getPageRequest());
    }
}
