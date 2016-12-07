package com.fzb.blog.web.controller.admin.api;

import com.fzb.blog.common.response.UpdateRecordResponse;
import com.fzb.blog.model.Type;
import com.fzb.blog.web.controller.BaseController;

import java.util.Map;

public class TypeController extends BaseController {
    public UpdateRecordResponse delete() {
        return new UpdateRecordResponse(Type.dao.deleteById(getPara("id")));
    }

    public Map index() {
        return Type.dao.queryAll(getParaToInt("page"), getParaToInt("rows"));
    }

    public UpdateRecordResponse add() {
        return new UpdateRecordResponse(new Type().set("typeName", getPara("typeName"))
                .set("alias", getPara("alias"))
                .set("remark", getPara("remark")).save());
    }

    public UpdateRecordResponse update() {
        return new UpdateRecordResponse(new Type().set("typeId", getPara("id"))
                .set("typeName", getPara("typeName"))
                .set("alias", getPara("alias"))
                .set("remark", getPara("remark")).update());
    }
}
