package com.zrlog.admin.web.controller.api;

import com.google.gson.Gson;
import com.hibegin.common.util.BeanUtil;
import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.ObjectHelpers;
import com.hibegin.common.util.StringUtils;
import com.hibegin.http.annotation.ResponseBody;
import com.hibegin.http.server.web.Controller;
import com.zrlog.admin.business.rest.request.LoginRequest;
import com.zrlog.admin.business.rest.response.IndexResponse;
import com.zrlog.admin.business.rest.response.StatisticsInfoResponse;
import com.zrlog.admin.business.rest.response.UpdateRecordResponse;
import com.zrlog.admin.business.rest.response.UserBasicInfoResponse;
import com.zrlog.admin.business.service.AdminArticleService;
import com.zrlog.admin.business.service.UserService;
import com.zrlog.admin.web.controller.page.AdminPageController;
import com.zrlog.blog.web.util.WebTools;
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
import com.zrlog.util.ZrLogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.*;

public class AdminController extends Controller {

    private final UserService userService = new UserService();

    @ResponseBody
    public ApiStandardResponse<UserBasicInfoResponse> login() throws SQLException {
        if (!InstallUtils.isInstalled()) {
            throw new MissingInstallException();
        }
        LoginRequest loginRequest = BeanUtil.convertWithValid(getRequest().getInputStream(), LoginRequest.class);
        UserBasicInfoResponse resp = userService.login(loginRequest, request);
        Constants.zrLogConfig.getTokenService().setAdminToken(new User().getUserByUserName(resp.getUserName().toLowerCase()), resp.getKey(), Objects.equals(loginRequest.getHttps(), true) ? "https" : "http", getRequest(), getResponse());
        return new ApiStandardResponse<>(resp);
    }

    @ResponseBody
    public Map<String, Object> manifest() throws IOException {
        try (InputStream inputStream = AdminPageController.class.getResourceAsStream("/admin/manifest.json")) {
            if (inputStream == null) {
                return new HashMap<>();
            }
            Map map = new Gson().fromJson(IOUtil.getStringInputStream(inputStream), Map.class);
            PublicInfoVO publicInfoVO = new CommonService().getPublicInfo(request);
            if (StringUtils.isNotEmpty(publicInfoVO.getWebsiteTitle())) {
                map.put("short_name", publicInfoVO.getWebsiteTitle());
            }
            map.put("name", Constants.getAdminTitle(""));
            map.put("theme_color", publicInfoVO.getPwaThemeColor());
            map.put("description", ObjectHelpers.requireNonNullElse(Constants.zrLogConfig.getPublicWebSite().get("description"), ""));
            map.put("id", Constants.getAppId());
            map.put("background_color", publicInfoVO.getAdmin_darkMode() ? "#000000" : "#FFFFFF");
            List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("icons");
            for (Map<String, Object> icon : list) {
                icon.put("src", WebTools.buildEncodedUrl(request, (String) icon.get("src")));
            }
            if (ZrLogUtil.isStaticPlugin(request)) {
                map.put("start_url", ((String) map.get("start_url")).replace("./index", "./index.html"));
            }
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
        Map<String, Object> map = new HashMap<>();
        map.put("message", ObjectHelpers.requireNonNullElse(request.getParaToStr("message"), ""));
        return new ApiStandardResponse<>(map);
    }

    @ResponseBody
    public ApiStandardResponse<Map<String, Object>> plugin() {
        return new ApiStandardResponse<>(new HashMap<>());
    }

    @ResponseBody
    public ApiStandardResponse<Void> dev() {
        System.getProperties().put("sws.run.mode", "dev");
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
                new ArrayList<>(Collections.singletonList(tips.get(0))),
                new AdminArticleService().activityDataList(), BlogBuildInfoUtil.getVersionInfo()));
    }
}
