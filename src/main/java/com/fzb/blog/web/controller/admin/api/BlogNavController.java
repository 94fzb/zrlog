package com.fzb.blog.web.controller.admin.api;

import com.fzb.blog.common.response.UpdateRecordResponse;
import com.fzb.blog.model.LogNav;
import com.fzb.blog.web.controller.BaseController;

import java.util.Map;

public class BlogNavController extends BaseController {

    public UpdateRecordResponse delete() {
        String[] ids = getPara("id").split(",");
        for (String id : ids) {
            LogNav.dao.deleteById(id);
        }
        return new UpdateRecordResponse();
    }

    public Map index() {
        return LogNav.dao.queryAll(getParaToInt("page"), getParaToInt("rows"));
    }

    public UpdateRecordResponse add() {
        new LogNav().set("navName", getPara("navName"))
                .set("url", getPara("url")).set("sort", getParaToInt("sort"))
                .save();
        return new UpdateRecordResponse();
    }

    public UpdateRecordResponse update() {
        UpdateRecordResponse recordResponse = new UpdateRecordResponse();
        boolean success = new LogNav().set("navId", getPara("id"))
                .set("navName", getPara("navName")).set("url", getPara("url"))
                .set("sort", getParaToInt("sort")).update();
        if (!success) {
            recordResponse.setError(1);
        }
        return recordResponse;
    }

}
