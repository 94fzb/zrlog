package com.zrlog.web.controller.admin.api;

import com.zrlog.common.response.UpdateRecordResponse;
import com.zrlog.model.LogNav;
import com.zrlog.web.annotation.RefreshCache;
import com.zrlog.web.controller.BaseController;

import java.util.Map;

public class BlogNavController extends BaseController {

    @RefreshCache
    public UpdateRecordResponse delete() {
        String[] ids = getPara("id").split(",");
        for (String id : ids) {
            LogNav.dao.deleteById(id);
        }
        return new UpdateRecordResponse();
    }

    public Map index() {
        return LogNav.dao.find(getPageable());
    }

    @RefreshCache
    public UpdateRecordResponse add() {
        return new UpdateRecordResponse(new LogNav().set("navName", getPara("navName"))
                .set("url", getPara("url")).set("sort", getParaToInt("sort"))
                .save());
    }

    @RefreshCache
    public UpdateRecordResponse update() {
        return new UpdateRecordResponse(new LogNav().set("navId", getPara("id"))
                .set("navName", getPara("navName")).set("url", getPara("url"))
                .set("sort", getParaToInt("sort")).update());
    }

}
