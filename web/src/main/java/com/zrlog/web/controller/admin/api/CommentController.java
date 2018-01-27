package com.zrlog.web.controller.admin.api;

import com.zrlog.common.request.ReadCommentRequest;
import com.zrlog.common.response.UpdateRecordResponse;
import com.zrlog.model.Comment;
import com.zrlog.util.ZrLogUtil;
import com.zrlog.web.controller.BaseController;

import java.util.HashMap;
import java.util.Map;

public class CommentController extends BaseController {

    public Map delete() {
        String[] ids = getPara("id").split(",");
        for (String id : ids) {
            Comment.dao.deleteById(id);
        }
        return new HashMap<String, Object>();
    }

    public UpdateRecordResponse read() {
        ReadCommentRequest commentRequest = ZrLogUtil.convertRequestBody(getRequest(), ReadCommentRequest.class);
        Comment.dao.doRead(commentRequest.getId());
        return new UpdateRecordResponse();
    }

    public Map index() {
        return Comment.dao.find(getPageable());
    }

    public UpdateRecordResponse update() {
        //TODO
        return new UpdateRecordResponse(true);
    }
}
