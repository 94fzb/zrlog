package com.zrlog.web.controller.admin.api;

import com.zrlog.common.request.ReadCommentRequest;
import com.zrlog.common.response.StandardResponse;
import com.zrlog.common.response.UpdateRecordResponse;
import com.zrlog.service.CommentService;
import com.zrlog.util.ZrLogUtil;
import com.zrlog.web.annotation.RefreshCache;
import com.zrlog.web.controller.BaseController;

import java.util.Map;

public class CommentController extends BaseController {

    private CommentService commentService = new CommentService();

    @RefreshCache
    public StandardResponse delete() {
        return commentService.delete(getPara("id").split(","));
    }

    public UpdateRecordResponse read() {
        return commentService.read(ZrLogUtil.convertRequestBody(getRequest(), ReadCommentRequest.class));
    }

    public Map index() {
        return commentService.page(getPageable());
    }

    @RefreshCache
    public UpdateRecordResponse update() {
        //TODO
        return new UpdateRecordResponse(true);
    }
}
