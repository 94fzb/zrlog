package com.zrlog.web.controller.admin.api;

import com.zrlog.common.response.UpdateRecordResponse;
import com.zrlog.model.Link;
import com.zrlog.model.Tag;
import com.zrlog.web.controller.BaseController;

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
