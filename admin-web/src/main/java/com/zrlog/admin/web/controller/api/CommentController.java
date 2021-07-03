package com.zrlog.admin.web.controller.api;

import com.jfinal.core.Controller;
import com.zrlog.admin.business.rest.request.ReadCommentRequest;
import com.zrlog.admin.business.rest.response.UpdateRecordResponse;
import com.zrlog.admin.business.service.AdminCommentService;
import com.zrlog.admin.business.util.ControllerUtil;
import com.zrlog.admin.web.annotation.RefreshCache;
import com.zrlog.common.rest.response.StandardResponse;
import com.zrlog.data.dto.PageData;
import com.zrlog.model.Comment;
import com.zrlog.util.ZrLogUtil;

public class CommentController extends Controller {

    private final AdminCommentService commentService = new AdminCommentService();

    @RefreshCache
    public StandardResponse delete() {
        return commentService.delete(getPara("id").split(","));
    }

    public UpdateRecordResponse read() {
        return commentService.read(ZrLogUtil.convertRequestBody(getRequest(), ReadCommentRequest.class));
    }

    public PageData<Comment> index() {
        return commentService.page(ControllerUtil.getPageRequest(this));
    }
}
