package com.zrlog.admin.web.controller.api;

import com.hibegin.common.util.BeanUtil;
import com.hibegin.http.annotation.ResponseBody;
import com.hibegin.http.server.web.Controller;
import com.zrlog.admin.business.rest.request.CreateLinkRequest;
import com.zrlog.admin.business.rest.request.UpdateLinkRequest;
import com.zrlog.admin.business.rest.response.UpdateRecordResponse;
import com.zrlog.admin.web.annotation.RefreshCache;
import com.zrlog.blog.web.util.ControllerUtil;
import com.zrlog.common.rest.response.ApiStandardResponse;
import com.zrlog.data.dto.PageData;
import com.zrlog.model.Link;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;

public class LinkController extends Controller {

    @RefreshCache(async = true)
    @ResponseBody
    public UpdateRecordResponse delete() throws SQLException {
        return new UpdateRecordResponse(new Link().deleteById(request.getParaToInt(("id"))));
    }

    @RefreshCache(async = true)
    @ResponseBody
    public UpdateRecordResponse update() throws IOException, SQLException {
        UpdateLinkRequest typeRequest = BeanUtil.convertWithValid(getRequest().getInputStream(), UpdateLinkRequest.class);
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
        CreateLinkRequest typeRequest = BeanUtil.convertWithValid(getRequest().getInputStream(), CreateLinkRequest.class);
        return new UpdateRecordResponse(new Link().set("linkName", typeRequest.getLinkName())
                .set("sort", Objects.requireNonNullElse(typeRequest.getSort(), 0))
                .set("url", typeRequest.getUrl()).set("alt", Objects.requireNonNullElse(typeRequest.getAlt(), "")).save());
    }

}
