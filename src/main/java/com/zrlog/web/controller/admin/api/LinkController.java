package com.zrlog.web.controller.admin.api;

import com.zrlog.common.response.UpdateRecordResponse;
import com.zrlog.model.Link;
import com.zrlog.web.controller.BaseController;

import java.util.Map;

public class LinkController extends BaseController {

    public UpdateRecordResponse delete() {
        return new UpdateRecordResponse(Link.dao.deleteById(getPara("id")));
    }

    public UpdateRecordResponse update() {
        if (getPara("id") != null) {
            Link.dao.set("linkId", getPara("id"))
                    .set("linkName", getPara("linkName"))
                    .set("sort", getParaToInt("sort", 100))
                    .set("url", getPara("url")).set("alt", getPara("alt")).update();
            return new UpdateRecordResponse();
        } else {
            return add();
        }
    }

    public Map index() {
        return Link.dao.queryAll(getParaToInt("page"), getParaToInt("rows"));
    }

    public UpdateRecordResponse add() {
        new Link().set("linkName", getPara("linkName"))
                .set("sort", getParaToInt("sort", 100))
                .set("url", getPara("url")).set("alt", getPara("alt")).save();
        return new UpdateRecordResponse();
    }

}
