package com.zrlog.admin.web.controller.api;

import com.hibegin.common.util.BeanUtil;
import com.zrlog.admin.web.annotation.RefreshCache;
import com.zrlog.blog.web.controller.BaseController;
import com.zrlog.business.rest.base.CreateNavRequest;
import com.zrlog.business.rest.base.UpdateNavRequest;
import com.zrlog.business.rest.response.UpdateRecordResponse;
import com.zrlog.data.dto.PageData;
import com.zrlog.model.LogNav;

import java.io.IOException;

public class BlogNavController extends BaseController {

    @RefreshCache
    public UpdateRecordResponse delete() {
        String[] ids = getPara("id").split(",");
        for (String id : ids) {
            new LogNav().deleteById(id);
        }
        return new UpdateRecordResponse();
    }

    public PageData<LogNav> index() {
        return new LogNav().find(getPageRequest());
    }

    @RefreshCache
    public UpdateRecordResponse add() throws IOException {
        CreateNavRequest createNavRequest = BeanUtil.convert(getRequest().getInputStream(), CreateNavRequest.class);
        return new UpdateRecordResponse(new LogNav().set("navName", createNavRequest.getNavName())
                .set("url", createNavRequest.getUrl()).set("sort", createNavRequest.getSort())
                .save());
    }

    @RefreshCache
    public UpdateRecordResponse update() throws IOException {
        UpdateNavRequest createNavRequest = BeanUtil.convert(getRequest().getInputStream(), UpdateNavRequest.class);
        return new UpdateRecordResponse(new LogNav().set("navId", createNavRequest.getId())
                .set("navName", createNavRequest.getNavName()).set("url", createNavRequest.getUrl())
                .set("sort", createNavRequest.getSort()).update());
    }

}