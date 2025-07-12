package com.zrlog.admin.web.controller.api;

import com.hibegin.common.dao.dto.PageData;
import com.hibegin.http.annotation.ResponseBody;
import com.zrlog.admin.business.rest.request.CreateNavRequest;
import com.zrlog.admin.business.rest.request.UpdateNavRequestRequest;
import com.zrlog.admin.business.rest.response.UpdateRecordResponse;
import com.zrlog.admin.web.annotation.RefreshCache;
import com.zrlog.business.service.TemplateHelper;
import com.zrlog.business.util.ControllerUtil;
import com.zrlog.common.Constants;
import com.zrlog.common.controller.BaseController;
import com.zrlog.common.rest.response.ApiStandardResponse;
import com.zrlog.model.LogNav;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;

public class BlogNavController extends BaseController {

    @RefreshCache(async = true)
    @ResponseBody
    public UpdateRecordResponse delete() throws SQLException {
        String idStr = getParamWithEmptyCheck("id");
        String[] ids = idStr.split(",");
        for (String id : ids) {
            new LogNav().deleteById(Integer.parseInt(id));
        }
        return new UpdateRecordResponse();
    }

    @ResponseBody
    public ApiStandardResponse<PageData<Map<String, Object>>> index() throws SQLException {
        PageData<Map<String, Object>> mapPageData = new LogNav().find(ControllerUtil.unPageRequest());
        mapPageData.getRows().forEach(e -> {
            e.put("jumpUrl", TemplateHelper.getNavUrl(request, Constants.getSuffix(request), (String) e.get("url")));
        });
        return new ApiStandardResponse<>(mapPageData);
    }

    @RefreshCache(async = true)
    @ResponseBody
    public UpdateRecordResponse add() throws IOException, SQLException {
        CreateNavRequest createNavRequest = getRequestBodyWithNullCheck(CreateNavRequest.class);
        return new UpdateRecordResponse(new LogNav().set("navName", createNavRequest.getNavName()).set("url",
                createNavRequest.getUrl()).set("sort", createNavRequest.getSort()).save());
    }

    @RefreshCache(async = true)
    @ResponseBody
    public UpdateRecordResponse update() throws IOException, SQLException {
        UpdateNavRequestRequest createNavRequest = getRequestBodyWithNullCheck(UpdateNavRequestRequest.class);
        return new UpdateRecordResponse(new LogNav()
                .set("navName", createNavRequest.getNavName())
                .set("url", createNavRequest.getUrl())
                .set("sort", Objects.requireNonNullElse(createNavRequest.getSort(), 0))
                .updateById(createNavRequest.getId()));
    }

}
