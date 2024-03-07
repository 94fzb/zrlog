package com.zrlog.admin.web.controller.api;

import com.hibegin.common.util.BeanUtil;
import com.hibegin.http.annotation.ResponseBody;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.hibegin.http.server.web.Controller;
import com.zrlog.admin.business.rest.base.CreateNavRequest;
import com.zrlog.admin.business.rest.base.UpdateNavRequest;
import com.zrlog.admin.business.rest.response.UpdateRecordResponse;
import com.zrlog.admin.business.util.ControllerUtil;
import com.zrlog.admin.web.annotation.RefreshCache;
import com.zrlog.common.rest.response.ApiStandardResponse;
import com.zrlog.data.dto.PageData;
import com.zrlog.model.LogNav;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class BlogNavController extends Controller {

    public BlogNavController() {
    }

    public BlogNavController(HttpRequest request, HttpResponse response) {
        super(request, response);
    }

    @RefreshCache(async = true)
    @ResponseBody
    public UpdateRecordResponse delete() throws SQLException {
        String[] ids = request.getParaToStr("id").split(",");
        for (String id : ids) {
            new LogNav().deleteById(Integer.parseInt(id));
        }
        return new UpdateRecordResponse();
    }

    @ResponseBody
    public ApiStandardResponse<PageData<Map<String,Object>>> index() throws SQLException {
        return new ApiStandardResponse<>(new LogNav().find(ControllerUtil.getPageRequest(this)));
    }

    @RefreshCache(async = true)
    @ResponseBody
    public UpdateRecordResponse add() throws IOException, SQLException {
        CreateNavRequest createNavRequest = BeanUtil.convert(getRequest().getInputStream(), CreateNavRequest.class);
        return new UpdateRecordResponse(new LogNav().set("navName", createNavRequest.getNavName()).set("url",
                createNavRequest.getUrl()).set("sort", createNavRequest.getSort()).save());
    }

    @RefreshCache(async = true)
    @ResponseBody
    public UpdateRecordResponse update() throws IOException, SQLException {
        UpdateNavRequest createNavRequest = BeanUtil.convert(getRequest().getInputStream(), UpdateNavRequest.class);

        return new UpdateRecordResponse(new LogNav()
                .set("navName", createNavRequest.getNavName())
                .set("url", createNavRequest.getUrl())
                .set("sort", createNavRequest.getSort())
                .updateById(createNavRequest.getId()));
    }

}
