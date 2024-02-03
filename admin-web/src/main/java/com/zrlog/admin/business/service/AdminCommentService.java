package com.zrlog.admin.business.service;

import com.zrlog.admin.business.rest.request.ReadCommentRequest;
import com.zrlog.admin.business.rest.response.UpdateRecordResponse;
import com.zrlog.common.rest.request.PageRequest;
import com.zrlog.common.rest.response.StandardResponse;
import com.zrlog.data.dto.PageData;
import com.zrlog.model.Comment;

import java.sql.SQLException;
import java.util.Map;

public class AdminCommentService {

    public StandardResponse delete(String[] ids) throws SQLException {
        for (String id : ids) {
            new Comment().deleteById(Integer.parseInt(id));
        }
        return new StandardResponse();
    }

    public UpdateRecordResponse read(ReadCommentRequest commentRequest) {
        new Comment().doRead(commentRequest.getId());
        return new UpdateRecordResponse();
    }

    public PageData<Map<String, Object>> page(PageRequest pageable) throws SQLException {
        return new Comment().find(pageable);
    }
}
