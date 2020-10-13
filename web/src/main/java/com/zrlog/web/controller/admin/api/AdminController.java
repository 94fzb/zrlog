package com.zrlog.web.controller.admin.api;

import com.zrlog.business.rest.request.LoginRequest;
import com.zrlog.business.rest.response.LoginResponse;
import com.zrlog.business.rest.response.UpdateRecordResponse;
import com.zrlog.model.Comment;
import com.zrlog.model.Log;
import com.zrlog.model.User;
import com.zrlog.business.service.UserService;
import com.zrlog.util.ZrLogUtil;
import com.zrlog.web.annotation.RefreshCache;
import com.zrlog.web.config.ZrLogConfig;
import com.zrlog.web.controller.BaseController;
import com.zrlog.web.token.AdminTokenService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class AdminController extends BaseController {

    private final UserService userService = new UserService();

    private final AdminTokenService adminTokenService = new AdminTokenService();

    private static final AtomicInteger sessionAtomicInteger = new AtomicInteger();

    public LoginResponse login() {
        LoginRequest loginRequest = ZrLogUtil.convertRequestBody(getRequest(), LoginRequest.class);
        LoginResponse login = userService.login(loginRequest);
        if (login.getError() == 0) {
            adminTokenService.setAdminToken(new User().getUserByUserName(loginRequest.getUserName().toLowerCase()),
                    sessionAtomicInteger.incrementAndGet(), loginRequest.getHttps() ? "https" : "http", getRequest(), getResponse());
        }
        return login;
    }

    /**
     * 插件调用这个方法
     */
    @RefreshCache
    public UpdateRecordResponse refreshCache() {
        return new UpdateRecordResponse();
    }

    public Map<String, Object> serverInfo() {
        Map<String, Object> info = new HashMap<>();
        ZrLogConfig.getSystemProp().forEach((key, value) -> info.put(key.toString(), value));
        ZrLogConfig.getBlogProp().forEach((key, value) -> info.put("zrlog." + key.toString(), value));
        return info;
    }

    public Map<String, Object> statisticsInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("commCount", new Comment().count());
        info.put("toDayCommCount", new Comment().countToDayComment());
        info.put("clickCount", new Log().sumClick());
        info.put("articleCount", new Log().adminCount());
        return info;
    }
}
