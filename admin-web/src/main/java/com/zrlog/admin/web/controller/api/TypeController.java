package com.zrlog.admin.web.controller.api;

import com.zrlog.business.rest.response.UpdateRecordResponse;
import com.zrlog.data.dto.PageData;
import com.zrlog.model.Type;
import com.zrlog.admin.web.annotation.RefreshCache;
import com.zrlog.blog.web.controller.BaseController;

public class TypeController extends BaseController {
    @RefreshCache
    public UpdateRecordResponse delete() {
        return new UpdateRecordResponse(new Type().deleteById(getPara("id")));
    }

    public PageData<Type> index() {
        return new Type().find(getPageRequest());
    }

    @RefreshCache
    public UpdateRecordResponse add() {
        return new UpdateRecordResponse(new Type().set("typeName", getPara("typeName"))
                .set("alias", getPara("alias"))
                .set("remark", getPara("remark")).save());
    }

    @RefreshCache
    public UpdateRecordResponse update() {
        return new UpdateRecordResponse(new Type().set("typeId", getPara("id"))
                .set("typeName", getPara("typeName"))
                .set("alias", getPara("alias"))
                .set("remark", getPara("remark")).update());
    }
}
