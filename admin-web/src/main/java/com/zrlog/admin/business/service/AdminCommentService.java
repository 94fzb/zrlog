package com.zrlog.admin.business.service;

import com.zrlog.admin.business.rest.request.ReadCommentRequest;
import com.zrlog.admin.business.rest.response.UpdateRecordResponse;
import com.zrlog.common.rest.request.PageRequest;
import com.zrlog.common.rest.response.StandardResponse;
import com.zrlog.data.dto.PageData;
import com.zrlog.model.Comment;

public class AdminCommentService {

    public StandardResponse delete(String[] ids) {
        for (String id : ids) {
            new Comment().deleteById(id);
        }
        return new StandardResponse();
    }

    public UpdateRecordResponse read(ReadCommentRequest commentRequest) {
        new Comment().doRead(commentRequest.getId());
        return new UpdateRecordResponse();
    }

    public PageData<Comment> page(PageRequest pageable) {
        return new Comment().find(pageable);
    }
}
