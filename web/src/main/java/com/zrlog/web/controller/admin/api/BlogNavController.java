package com.zrlog.web.controller.admin.api;

import com.zrlog.business.rest.response.UpdateRecordResponse;
import com.zrlog.data.dto.PageData;
import com.zrlog.model.LogNav;
import com.zrlog.web.annotation.RefreshCache;
import com.zrlog.web.controller.BaseController;

public class BlogNavController extends BaseController {

    @RefreshCache
    public UpdateRecordResponse delete() {
        String[] ids = getPara("id").split(",");
        for (String id : ids) {
            new LogNav().deleteById(id);
        }
        return new UpdateRecordResponse();
    }

    public PageData<LogNav> index() {
        return new LogNav().find(getPageRequest());
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
