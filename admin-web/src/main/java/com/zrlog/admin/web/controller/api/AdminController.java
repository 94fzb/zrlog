package com.zrlog.admin.web.controller.api;

import com.google.gson.Gson;
import com.hibegin.common.util.BeanUtil;
import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.StringUtils;
import com.hibegin.http.annotation.ResponseBody;
import com.hibegin.http.server.web.Controller;
import com.zrlog.admin.business.rest.request.LoginRequest;
import com.zrlog.admin.business.rest.response.*;
import com.zrlog.admin.business.service.AdminArticleService;
import com.zrlog.admin.business.service.UserService;
import com.zrlog.admin.util.SystemInfoUtils;
import com.zrlog.admin.web.controller.page.AdminPageController;
import com.zrlog.business.exception.MissingInstallException;
import com.zrlog.business.rest.response.PublicInfoVO;
import com.zrlog.business.service.CommonService;
import com.zrlog.business.util.InstallUtils;
import com.zrlog.common.Constants;
import com.zrlog.common.rest.response.ApiStandardResponse;
import com.zrlog.model.Comment;
import com.zrlog.model.Log;
import com.zrlog.model.User;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.I18nUtil;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.*;

public class AdminController extends Controller {

    private final UserService userService = new UserService();

    @ResponseBody
    public ApiStandardResponse<LoginResponse> login() throws SQLException {
        if (!InstallUtils.isInstalled()) {
            throw new MissingInstallException();
        }
        LoginRequest loginRequest = BeanUtil.convertWithValid(getRequest().getInputStream(), LoginRequest.class);
        userService.login(loginRequest);
        String key = UUID.randomUUID().toString();
        Constants.zrLogConfig.getTokenService().setAdminToken(new User().getUserByUserName(loginRequest.getUserName().toLowerCase()), key, Objects.equals(loginRequest.getHttps(), true) ? "https" : "http", getRequest(), getResponse());
        return new ApiStandardResponse<>(new LoginResponse(key));
    }

    @ResponseBody
    public Map<String, Object> manifest() throws IOException {
        try (InputStream inputStream = AdminPageController.class.getResourceAsStream("/admin/manifest.json")) {
            if (inputStream == null) {
                return new HashMap<>();
            }
            Map map = new Gson().fromJson(IOUtil.getStringInputStream(inputStream), Map.class);
            PublicInfoVO publicInfoVO = new CommonService().getPublicInfo(request);
            if (StringUtils.isNotEmpty(publicInfoVO.websiteTitle())) {
                map.put("short_name", publicInfoVO.websiteTitle());
            }
            map.put("name", Constants.getAdminTitle(""));
            map.put("theme_color", publicInfoVO.pwaThemeColor());
            map.put("description", Objects.requireNonNullElse(Constants.zrLogConfig.getPublicWebSite().get("description"), ""));
            map.put("id", Constants.getAppId());
            map.put("background_color", publicInfoVO.admin_darkMode() ? "#000000" : "#FFFFFF");
            return map;
        }
    }

    /**
     * 触发更新缓存
     */
    @ResponseBody
    public UpdateRecordResponse refreshCache() {
        Constants.zrLogConfig.getCacheService().refreshInitDataCacheAsync(request, true);
        return new UpdateRecordResponse();
    }

    private StatisticsInfoResponse statisticsInfo() throws SQLException {
        StatisticsInfoResponse info = new StatisticsInfoResponse();
        info.setCommCount(new Comment().count());
        info.setToDayCommCount(new Comment().countToDayComment());
        info.setClickCount(new Log().sumClick().longValue());
        info.setArticleCount(new Log().getAdminCount());
        return info;
    }

    @ResponseBody
    public ApiStandardResponse<Map<String, Object>> error() {
        return new ApiStandardResponse<>(Map.of("message", Objects.requireNonNullElse(request.getParaToStr("message"), "")));
    }

    @ResponseBody
    public ApiStandardResponse<Map<String, Object>> plugin() {
        return new ApiStandardResponse<>(new HashMap<>());
    }

    @ResponseBody
    public ApiStandardResponse<Void> dev() {
        Constants.devEnabled = true;
        return new ApiStandardResponse<>();
    }

    @ResponseBody
    public ApiStandardResponse<IndexResponse> index() throws SQLException {
        List<String> tips = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            tips.add(I18nUtil.getBackendStringFromRes("admin.index.welcomeTips_" + i));
        }
        Collections.shuffle(tips);
        return new ApiStandardResponse<>(new IndexResponse(statisticsInfo(),
                I18nUtil.getBackendStringFromRes("admin.index.welcomeTip"),
                new ArrayList<>(Collections.singletonList(tips.getFirst())),
                new AdminArticleService().activityDataList(), BlogBuildInfoUtil.getVersionInfo()));
    }
}
