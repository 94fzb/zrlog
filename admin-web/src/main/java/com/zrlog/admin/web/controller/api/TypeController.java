package com.zrlog.admin.web.controller.api;

import com.hibegin.common.util.BeanUtil;
import com.hibegin.http.annotation.ResponseBody;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.hibegin.http.server.web.Controller;
import com.zrlog.admin.business.exception.DeleteTypeException;
import com.zrlog.admin.business.rest.request.CreateTypeRequest;
import com.zrlog.admin.business.rest.request.UpdateTypeRequest;
import com.zrlog.admin.business.rest.response.ArticleTypeResponseEntry;
import com.zrlog.admin.business.rest.response.UpdateRecordResponse;
import com.zrlog.admin.business.service.ArticleTypeService;
import com.zrlog.admin.business.util.ControllerUtil;
import com.zrlog.admin.web.annotation.RefreshCache;
import com.zrlog.common.Constants;
import com.zrlog.common.rest.response.ApiStandardResponse;
import com.zrlog.data.dto.PageData;
import com.zrlog.model.Log;
import com.zrlog.model.Type;
import com.zrlog.util.ZrLogUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

public class TypeController extends Controller {

    @RefreshCache(async = true)
    @ResponseBody
    public UpdateRecordResponse delete() throws SQLException {
        int typeId = getRequest().getParaToInt("id");
        if (new Log().countByTypeId(typeId) > 0) {
            throw new DeleteTypeException();
        }
        return new UpdateRecordResponse(new Type().deleteById(typeId));
    }

    @ResponseBody
    public ApiStandardResponse<PageData<ArticleTypeResponseEntry>> index() throws SQLException {
        return new ApiStandardResponse<>(new ArticleTypeService().find(ZrLogUtil.getHomeUrlWithHost(request), ControllerUtil.unPageRequest(), Constants.isStaticHtmlStatus()));
    }

    @RefreshCache(async = true)
    @ResponseBody
    public UpdateRecordResponse add() throws IOException, SQLException {
        CreateTypeRequest typeRequest = BeanUtil.convertWithValid(getRequest().getInputStream(), CreateTypeRequest.class);
        return new UpdateRecordResponse(new Type().set("typeName", typeRequest.getTypeName()).set("alias",
                typeRequest.getAlias()).set("remark", Objects.requireNonNullElse(typeRequest.getRemark(), "")).save());
    }

    @RefreshCache(async = true)
    @ResponseBody
    public UpdateRecordResponse update() throws IOException, SQLException {
        UpdateTypeRequest typeRequest = BeanUtil.convertWithValid(getRequest().getInputStream(), UpdateTypeRequest.class);
        return new UpdateRecordResponse(new Type()
                .set("typeName", typeRequest.getTypeName())
                .set("alias", typeRequest.getAlias())
                .set("remark", Objects.requireNonNullElse(typeRequest.getRemark(), ""))
                .updateById(typeRequest.getId()));
    }
}
