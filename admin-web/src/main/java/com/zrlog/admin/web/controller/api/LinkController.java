package com.zrlog.admin.web.controller.api;

import com.zrlog.business.rest.response.UpdateRecordResponse;
import com.zrlog.data.dto.PageData;
import com.zrlog.model.Link;
import com.zrlog.admin.web.annotation.RefreshCache;
import com.zrlog.blog.web.controller.BaseController;

public class LinkController extends BaseController {

    @RefreshCache
    public UpdateRecordResponse delete() {
        return new UpdateRecordResponse(new Link().deleteById(getPara("id")));
    }

    @RefreshCache
    public UpdateRecordResponse update() {
        if (getPara("id") != null) {
            new Link().set("linkId", getPara("id"))
                    .set("linkName", getPara("linkName"))
                    .set("sort", getParaToInt("sort", 100))
                    .set("url", getPara("url")).set("alt", getPara("alt")).update();
            return new UpdateRecordResponse();
        } else {
            return add();
        }
    }

    public PageData<Link> index() {
        return new Link().find(getPageRequest());
    }

    @RefreshCache
    public UpdateRecordResponse add() {
        new Link().set("linkName", getPara("linkName"))
                .set("sort", getParaToInt("sort", 100))
                .set("url", getPara("url")).set("alt", getPara("alt")).save();
        return new UpdateRecordResponse();
    }

}
