package com.zrlog.admin.web.controller.api;

import com.hibegin.http.annotation.ResponseBody;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.hibegin.http.server.web.Controller;
import com.zrlog.admin.business.rest.request.LoginRequest;
import com.zrlog.admin.business.rest.response.IndexResponse;
import com.zrlog.admin.business.rest.response.LoginResponse;
import com.zrlog.admin.business.rest.response.StatisticsInfoResponse;
import com.zrlog.admin.business.rest.response.UpdateRecordResponse;
import com.zrlog.admin.business.service.UserService;
import com.zrlog.admin.web.annotation.RefreshCache;
import com.zrlog.admin.web.token.AdminTokenService;
import com.zrlog.business.util.InstallUtils;
import com.zrlog.common.rest.response.ApiStandardResponse;
import com.zrlog.model.Comment;
import com.zrlog.model.Log;
import com.zrlog.model.User;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.ZrLogUtil;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class AdminController extends Controller {

    private final UserService userService = new UserService();

    private final AdminTokenService adminTokenService = new AdminTokenService();

    public AdminController() {
    }

    public AdminController(HttpRequest request, HttpResponse response) {
        super(request, response);
    }

    @ResponseBody
    public ApiStandardResponse<Map<String,Object>> login() throws SQLException {
        LoginRequest loginRequest = ZrLogUtil.convertRequestBody(getRequest(), LoginRequest.class);
        userService.login(loginRequest);
        String key = UUID.randomUUID().toString();
        adminTokenService.setAdminToken(new User().getUserByUserName(loginRequest.getUserName().toLowerCase()),
                key, loginRequest.getHttps() ? "https" : "http", getRequest(),
                getResponse());
        return new ApiStandardResponse<>(Map.of("key",key));
    }

    /**
     * 插件调用这个方法
     */
    @RefreshCache
    @ResponseBody
    public UpdateRecordResponse refreshCache() {
        return new UpdateRecordResponse();
    }

    private ApiStandardResponse<Map<String, Object>> serverInfo() {
        Map<String, Object> info = new HashMap<>();
        InstallUtils.getSystemProp().forEach((key, value) -> info.put(key.toString(), value));
        BlogBuildInfoUtil.getBlogProp().forEach((key, value) -> info.put("zrlog." + key.toString(), value));
        return new ApiStandardResponse<>(info);
    }

    private ApiStandardResponse<StatisticsInfoResponse> statisticsInfo() throws SQLException {
        StatisticsInfoResponse info = new StatisticsInfoResponse();
        info.setCommCount(new Comment().count());
        info.setToDayCommCount(new Comment().countToDayComment());
        info.setClickCount(new Log().sumClick().longValue());
        info.setArticleCount(new Log().adminCount());
        return new ApiStandardResponse<>(info);
    }

    @ResponseBody
    public ApiStandardResponse<Map<String,Object>> plugin(){
        return new ApiStandardResponse<>(new HashMap<>());
    }

    @ResponseBody
    public ApiStandardResponse<IndexResponse> index() throws SQLException {
        return new ApiStandardResponse<>(new IndexResponse(statisticsInfo().getData(),serverInfo().getData()));
    }
}
