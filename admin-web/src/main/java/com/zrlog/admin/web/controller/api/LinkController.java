package com.zrlog.admin.web.controller.api;

import com.hibegin.common.util.BeanUtil;
import com.hibegin.http.annotation.ResponseBody;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.hibegin.http.server.web.Controller;
import com.zrlog.admin.business.rest.base.CreateLinkRequest;
import com.zrlog.admin.business.rest.base.UpdateLinkRequest;
import com.zrlog.admin.business.rest.response.UpdateRecordResponse;
import com.zrlog.admin.business.util.ControllerUtil;
import com.zrlog.admin.web.annotation.RefreshCache;
import com.zrlog.common.rest.response.ApiStandardResponse;
import com.zrlog.data.dto.PageData;
import com.zrlog.model.Link;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class LinkController extends Controller {

    public LinkController() {
    }

    public LinkController(HttpRequest request, HttpResponse response) {
        super(request, response);
    }

    @RefreshCache(async = true)
    @ResponseBody
    public UpdateRecordResponse delete() throws SQLException {
        return new UpdateRecordResponse(new Link().deleteById(request.getParaToInt(("id"))));
    }

    @RefreshCache(async = true)
    @ResponseBody
    public UpdateRecordResponse update() throws IOException, SQLException {
        UpdateLinkRequest typeRequest = BeanUtil.convert(getRequest().getInputStream(), UpdateLinkRequest.class);
        new Link().set("linkName", typeRequest.getLinkName())
                .set("sort", typeRequest.getSort())
                .set("url", typeRequest.getUrl())
                .set("alt", typeRequest.getAlt())
                .updateById(typeRequest.getId());
        return new UpdateRecordResponse();
    }

    @ResponseBody
    public ApiStandardResponse<PageData<Map<String,Object>>> index() throws SQLException {
        return new ApiStandardResponse<>(new Link().find(ControllerUtil.getPageRequest(this)));
    }

    @RefreshCache(async = true)
    @ResponseBody
    public UpdateRecordResponse add() throws IOException, SQLException {
        CreateLinkRequest typeRequest = BeanUtil.convert(getRequest().getInputStream(), CreateLinkRequest.class);
        new Link().set("linkName", typeRequest.getLinkName()).set("sort", typeRequest.getSort()).set("url",
                typeRequest.getUrl()).set("alt", typeRequest.getAlt()).save();
        return new UpdateRecordResponse();
    }

}
