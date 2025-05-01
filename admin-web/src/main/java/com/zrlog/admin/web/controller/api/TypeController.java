package com.zrlog.admin.web.controller.api;

import com.hibegin.common.dao.dto.PageData;
import com.hibegin.http.annotation.ResponseBody;
import com.zrlog.common.controller.BaseController;
import com.zrlog.common.exception.ArgsException;
import com.zrlog.admin.business.exception.DeleteTypeException;
import com.zrlog.admin.business.rest.request.CreateTypeRequest;
import com.zrlog.admin.business.rest.request.UpdateTypeRequest;
import com.zrlog.admin.business.rest.response.ArticleTypeResponseEntry;
import com.zrlog.admin.business.rest.response.UpdateRecordResponse;
import com.zrlog.admin.business.service.ArticleTypeService;
import com.zrlog.admin.web.annotation.RefreshCache;
import com.zrlog.business.util.ControllerUtil;
import com.zrlog.common.Constants;
import com.zrlog.common.rest.response.ApiStandardResponse;
import com.zrlog.model.Log;
import com.zrlog.model.Type;
import com.zrlog.util.ZrLogUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

public class TypeController extends BaseController {

    @RefreshCache(async = true)
    @ResponseBody
    public UpdateRecordResponse delete() throws SQLException {
        int typeId = Integer.parseInt(getParamWithEmptyCheck("id"));
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
        CreateTypeRequest requestBody = getRequestBodyWithNullCheck(CreateTypeRequest.class);
        return new UpdateRecordResponse(new Type().set("typeName", requestBody.getTypeName())
                .set("alias", requestBody.getAlias()).set("remark", Objects.requireNonNullElse(requestBody.getRemark(), "")).save());
    }


    @RefreshCache(async = true)
    @ResponseBody
    public UpdateRecordResponse update() throws IOException, SQLException {
        UpdateTypeRequest requestBody = getRequestBodyWithNullCheck(UpdateTypeRequest.class);
        return new UpdateRecordResponse(new Type()
                .set("typeName", requestBody.getTypeName())
                .set("alias", requestBody.getAlias())
                .set("remark", Objects.requireNonNullElse(requestBody.getRemark(), ""))
                .updateById(requestBody.getId()));
    }
}
