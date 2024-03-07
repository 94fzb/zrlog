package com.zrlog.admin.web.controller.api;

import com.hibegin.common.util.BeanUtil;
import com.hibegin.http.annotation.ResponseBody;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.hibegin.http.server.web.Controller;
import com.zrlog.admin.business.exception.ArgsException;
import com.zrlog.admin.business.exception.DeleteTypeException;
import com.zrlog.admin.business.rest.base.CreateTypeRequest;
import com.zrlog.admin.business.rest.base.UpdateTypeRequest;
import com.zrlog.admin.business.rest.response.UpdateRecordResponse;
import com.zrlog.admin.business.util.ControllerUtil;
import com.zrlog.admin.web.annotation.RefreshCache;
import com.zrlog.common.rest.response.ApiStandardResponse;
import com.zrlog.data.dto.PageData;
import com.zrlog.model.Log;
import com.zrlog.model.Type;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class TypeController extends Controller {

    public TypeController() {
    }

    public TypeController(HttpRequest request, HttpResponse response) {
        super(request, response);
    }

    @RefreshCache(async = true)
    @ResponseBody
    public UpdateRecordResponse delete() throws SQLException {
        int typeId = getRequest().getParaToInt("id");
        if(new Log().countByTypeId(typeId) > 0){
            throw new DeleteTypeException();
        }
        return new UpdateRecordResponse(new Type().deleteById(typeId));
    }

    @ResponseBody
    public ApiStandardResponse<PageData<Map<String,Object>>> index() throws SQLException {
        return new ApiStandardResponse<>(new Type().find(ControllerUtil.getPageRequest(this)));
    }

    @RefreshCache(async = true)
    @ResponseBody
    public UpdateRecordResponse add() throws IOException, SQLException {
        CreateTypeRequest typeRequest = BeanUtil.convert(getRequest().getInputStream(), CreateTypeRequest.class);
        return new UpdateRecordResponse(new Type().set("typeName", typeRequest.getTypeName()).set("alias",
                typeRequest.getAlias()).set("remark", typeRequest.getRemark()).save());
    }

    @RefreshCache(async = true)
    @ResponseBody
    public UpdateRecordResponse update() throws IOException, SQLException {
        UpdateTypeRequest typeRequest = BeanUtil.convert(getRequest().getInputStream(), UpdateTypeRequest.class);
        return new UpdateRecordResponse(new Type()
                .set("typeName", typeRequest.getTypeName())
                .set("alias", typeRequest.getAlias())
                .set("remark", typeRequest.getRemark())
                .updateById(typeRequest.getId()));
    }
}
