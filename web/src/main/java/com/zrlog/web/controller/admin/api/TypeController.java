package com.zrlog.web.controller.admin.api;

import com.zrlog.common.response.UpdateRecordResponse;
import com.zrlog.model.Type;
import com.zrlog.web.annotation.RefreshCache;
import com.zrlog.web.controller.BaseController;

import java.util.Map;

public class TypeController extends BaseController {
    @RefreshCache
    public UpdateRecordResponse delete() {
        return new UpdateRecordResponse(Type.dao.deleteById(getPara("id")));
    }

    public Map index() {
        return Type.dao.find(getPageable());
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
