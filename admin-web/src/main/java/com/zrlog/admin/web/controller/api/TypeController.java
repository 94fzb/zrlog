package com.zrlog.admin.web.controller.api;

import com.hibegin.common.util.BeanUtil;
import com.jfinal.core.Controller;
import com.zrlog.admin.business.rest.base.CreateTypeRequest;
import com.zrlog.admin.business.rest.base.UpdateTypeRequest;
import com.zrlog.admin.business.rest.response.UpdateRecordResponse;
import com.zrlog.admin.business.util.ControllerUtil;
import com.zrlog.admin.web.annotation.RefreshCache;
import com.zrlog.data.dto.PageData;
import com.zrlog.model.Type;

import java.io.IOException;

public class TypeController extends Controller {

    @RefreshCache(async = true)
    public UpdateRecordResponse delete() {
        return new UpdateRecordResponse(new Type().deleteById(getPara("id")));
    }

    public PageData<Type> index() {
        return new Type().find(ControllerUtil.getPageRequest(this));
    }

    @RefreshCache(async = true)
    public UpdateRecordResponse add() throws IOException {
        CreateTypeRequest typeRequest = BeanUtil.convert(getRequest().getInputStream(), CreateTypeRequest.class);
        return new UpdateRecordResponse(new Type().set("typeName", typeRequest.getTypeName()).set("alias",
                typeRequest.getAlias()).set("remark", typeRequest.getRemark()).save());
    }

    @RefreshCache(async = true)
    public UpdateRecordResponse update() throws IOException {
        UpdateTypeRequest typeRequest = BeanUtil.convert(getRequest().getInputStream(), UpdateTypeRequest.class);
        return new UpdateRecordResponse(new Type().set("typeId", typeRequest.getId()).set("typeName",
                typeRequest.getTypeName()).set("alias", typeRequest.getAlias()).set("remark",
                typeRequest.getRemark()).update());
    }
}
