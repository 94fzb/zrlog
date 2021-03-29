package com.zrlog.admin.web.controller.api;

import com.hibegin.common.util.BeanUtil;
import com.zrlog.admin.web.annotation.RefreshCache;
import com.zrlog.blog.web.controller.BaseController;
import com.zrlog.admin.business.rest.base.CreateLinkRequest;
import com.zrlog.admin.business.rest.base.UpdateLinkRequest;
import com.zrlog.admin.business.rest.response.UpdateRecordResponse;
import com.zrlog.data.dto.PageData;
import com.zrlog.model.Link;

import java.io.IOException;

public class LinkController extends BaseController {

    @RefreshCache
    public UpdateRecordResponse delete() {
        return new UpdateRecordResponse(new Link().deleteById(getPara("id")));
    }

    @RefreshCache
    public UpdateRecordResponse update() throws IOException {
        UpdateLinkRequest typeRequest = BeanUtil.convert(getRequest().getInputStream(), UpdateLinkRequest.class);
        new Link().set("linkId", typeRequest.getId())
                .set("linkName", typeRequest.getLinkName())
                .set("sort", typeRequest.getSort())
                .set("url", typeRequest.getUrl())
                .set("alt", typeRequest.getAlt()).update();
        return new UpdateRecordResponse();
    }

    public PageData<Link> index() {
        return new Link().find(getPageRequest());
    }

    @RefreshCache
    public UpdateRecordResponse add() throws IOException {
        CreateLinkRequest typeRequest = BeanUtil.convert(getRequest().getInputStream(), CreateLinkRequest.class);
        new Link().set("linkName", typeRequest.getLinkName())
                .set("sort", typeRequest.getSort())
                .set("url", typeRequest.getUrl())
                .set("alt", typeRequest.getAlt()).save();
        return new UpdateRecordResponse();
    }

}
