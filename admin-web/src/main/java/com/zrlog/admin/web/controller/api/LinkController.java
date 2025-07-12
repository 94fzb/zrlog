package com.zrlog.admin.web.controller.api;

import com.hibegin.common.dao.dto.PageData;
import com.hibegin.http.annotation.ResponseBody;
import com.zrlog.admin.business.rest.request.CreateLinkRequest;
import com.zrlog.admin.business.rest.request.UpdateLinkRequest;
import com.zrlog.admin.business.rest.response.UpdateRecordResponse;
import com.zrlog.admin.web.annotation.RefreshCache;
import com.zrlog.business.util.ControllerUtil;
import com.zrlog.common.controller.BaseController;
import com.zrlog.common.exception.ArgsException;
import com.zrlog.common.rest.response.ApiStandardResponse;
import com.zrlog.model.Link;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;

public class LinkController extends BaseController {

    @RefreshCache(async = true)
    @ResponseBody
    public UpdateRecordResponse delete() throws SQLException {
        Integer id = request.getParaToInt("id");
        if (Objects.isNull(id) || id <= 0) {
            throw new ArgsException("id");
        }
        return new UpdateRecordResponse(new Link().deleteById(id));
    }

    @RefreshCache(async = true)
    @ResponseBody
    public UpdateRecordResponse update() throws IOException, SQLException {
        UpdateLinkRequest typeRequest = getRequestBodyWithNullCheck( UpdateLinkRequest.class);
        new Link().set("linkName", typeRequest.getLinkName())
                .set("sort", typeRequest.getSort())
                .set("url", typeRequest.getUrl())
                .set("alt", Objects.requireNonNullElse(typeRequest.getAlt(), ""))
                .updateById(typeRequest.getId());
        return new UpdateRecordResponse();
    }

    @ResponseBody
    public ApiStandardResponse<PageData<Map<String, Object>>> index() throws SQLException {
        return new ApiStandardResponse<>(new Link().find(ControllerUtil.unPageRequest()));
    }

    @RefreshCache(async = true)
    @ResponseBody
    public UpdateRecordResponse add() throws IOException, SQLException {
        CreateLinkRequest typeRequest = getRequestBodyWithNullCheck( CreateLinkRequest.class);
        return new UpdateRecordResponse(new Link().set("linkName", typeRequest.getLinkName())
                .set("sort", Objects.requireNonNullElse(typeRequest.getSort(), 0))
                .set("url", typeRequest.getUrl()).set("alt", Objects.requireNonNullElse(typeRequest.getAlt(), "")).save());
    }

}
