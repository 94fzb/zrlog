package com.zrlog.admin.web.controller.page;

import com.hibegin.http.annotation.ResponseBody;
import com.hibegin.http.server.web.Controller;
import com.zrlog.admin.business.AdminConstants;
import com.zrlog.admin.business.rest.response.ServerSideDataResponse;
import com.zrlog.admin.business.service.AdminPageService;
import com.zrlog.common.Constants;
import com.zrlog.common.rest.response.ApiStandardResponse;

import java.io.InputStream;
import java.util.Objects;

public class AdminPageController extends Controller {

    private final AdminPageService adminPageService = new AdminPageService();

    public void index() throws Throwable {
        if (Constants.zrLogConfig.isStaticPluginRequest(request)) {
            renderIndex();
            return;
        }
        if (Objects.equals(request.getUri(), Constants.ADMIN_URI_BASE_PATH) || Objects.equals(request.getUri(), Constants.ADMIN_URI_BASE_PATH + "/")) {
            response.redirect(Constants.ADMIN_URI_BASE_PATH + AdminConstants.INDEX_URI_PATH);
            return;
        }
        renderIndex();
    }


    private void renderIndex() throws Throwable {
        InputStream inputStream = AdminPageController.class.getResourceAsStream(AdminConstants.ADMIN_HTML_PAGE);
        if (Objects.isNull(inputStream)) {
            response.renderCode(404);
            return;
        }
        String html = adminPageService.buildHtml(request, response, inputStream);
        response.renderHtmlStr(html);
        if (Constants.zrLogConfig.isStaticPluginRequest(request)) {
            request.getAttr().put(Constants.STATIC_SITE_PLUGIN_HTML_FILE_KEY, Constants.zrLogConfig.getCacheService().saveResponseBodyToHtml(request, html));
        }
    }

    @ResponseBody
    public ApiStandardResponse<ServerSideDataResponse<Object>> ssJson() throws Throwable {
        return new ApiStandardResponse<>(adminPageService.serverSide(request.getParaToStr("uri"), request, response));
    }

    public void logout() {
        Constants.zrLogConfig.getTokenService().removeAdminToken(request, response);
    }
}
