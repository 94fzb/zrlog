package com.fzb.blog.web.controller.admin.api;

import com.fzb.blog.common.response.UpdateRecordResponse;
import com.fzb.blog.model.Link;
import com.fzb.blog.model.Tag;
import com.fzb.blog.web.controller.BaseController;

import java.util.Map;

public class TagController extends BaseController {
    public UpdateRecordResponse delete() {
        Link.dao.deleteById(getPara(0));
        return new UpdateRecordResponse();
    }

    public Map index() {
        Tag.dao.refreshTag();
        return Tag.dao.queryAll(getParaToInt("page"), getParaToInt("rows"));
    }
}
