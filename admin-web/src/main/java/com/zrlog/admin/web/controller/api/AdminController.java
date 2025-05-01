package com.zrlog.admin.web.controller.api;

import com.google.gson.Gson;
import com.hibegin.common.util.EnvKit;
import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.StringUtils;
import com.hibegin.http.annotation.ResponseBody;
import com.hibegin.http.server.config.ServerConfig;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.admin.business.AdminConstants;
import com.zrlog.admin.business.dto.UserLoginDTO;
import com.zrlog.admin.business.rest.request.LoginRequest;
import com.zrlog.admin.business.rest.response.*;
import com.zrlog.admin.business.service.AdminArticleService;
import com.zrlog.admin.business.service.AdminStatisticsService;
import com.zrlog.admin.business.service.UserService;
import com.zrlog.admin.web.annotation.RefreshCache;
import com.zrlog.admin.web.controller.page.AdminPageController;
import com.zrlog.blog.web.util.WebTools;
import com.zrlog.business.exception.MissingInstallException;
import com.zrlog.business.plugin.StaticSitePlugin;
import com.zrlog.business.rest.response.PublicInfoVO;
import com.zrlog.business.service.CommonService;
import com.zrlog.common.Constants;
import com.zrlog.common.controller.BaseController;

import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.ThreadUtils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import static com.zrlog.util.CrossUtils.cross;

public class AdminController extends BaseController {

    private final UserService userService = new UserService();

    @ResponseBody
    public AdminApiPageDataStandardResponse<UserBasicInfoResponse> login() throws Exception {
        if (!Constants.zrLogConfig.isInstalled()) {
            throw new MissingInstallException();
        }
        LoginRequest loginRequest = getRequestBodyWithNullCheck(LoginRequest.class);
        UserLoginDTO dto = userService.login(loginRequest, request);
        Constants.zrLogConfig.getTokenService().setAdminToken(dto.getId(), dto.getSecretKey(), dto.getUserBasicInfoResponse().getKey(),
                Objects.equals(loginRequest.getHttps(), true) ? "https" : "http", getRequest(), getResponse());
        return new AdminApiPageDataStandardResponse<>(dto.getUserBasicInfoResponse());
    }

    @ResponseBody
    public AdminApiPageDataStandardResponse<Map<String, Object>> adminResource() {
        cross(request, response);
        return new AdminApiPageDataStandardResponse<>(AdminConstants.adminResource.adminResourceInfo(request));
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
            map.put("name", AdminConstants.getAdminDocumentTitleByUri("/"));
            map.put("theme_color", publicInfoVO.getPwaThemeColor());
            map.put("description", Constants.getStringByFromWebSite("description", ""));
            map.put("id", Constants.getAppId());
            map.put("background_color", publicInfoVO.getAdmin_darkMode() ? "#000000" : "#FFFFFF");
            List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("icons");
            for (Map<String, Object> icon : list) {
                icon.put("src", WebTools.buildEncodedUrl(request, (String) icon.get("src")));
            }
            if (StaticSitePlugin.isStaticPluginRequest(request)) {
                map.put("start_url", ((String) map.get("start_url")).replace("./index", "./index.html"));
            }
            return map;
        }
    }

    /**
     * 触发更新缓存
     */
    @ResponseBody
    @RefreshCache(async = true)
    public UpdateRecordResponse refreshCache() {
        return new UpdateRecordResponse();
    }


    @ResponseBody
    public AdminApiPageDataStandardResponse<Map<String, Object>> error() {
        Map<String, Object> map = new HashMap<>();
        map.put("message", request.getParaToStr("message", ""));
        return new AdminApiPageDataStandardResponse<>(map);
    }

    @ResponseBody
    public AdminApiPageDataStandardResponse<Map<String, Object>> plugin() {
        String page = getRequest().getParaToStr("page", "");
        Map<String, Object> data = new HashMap<>();
        data.put("includePagePath", "admin/plugins/" + page);
        return new AdminApiPageDataStandardResponse<>(data);
    }

    @ResponseBody
    public AdminApiPageDataStandardResponse<Void> dev() {
        if (!request.getServerConfig().isNativeImageAgent()) {
            System.getProperties().put("sws.run.mode", "dev");
        }
        configDev(request.getServerConfig());
        return new AdminApiPageDataStandardResponse<>();
    }

    public static void configDev(ServerConfig serverConfig) {
        serverConfig.addLocalFileStaticResourceMapper(AdminConstants.ADMIN_DEV_URI_BASE_PATH, PathUtil.getRootPath(), true);
        if (EnvKit.isLambda()) {
            serverConfig.addLocalFileStaticResourceMapper(AdminConstants.ADMIN_DEV_URI_BASE_PATH + "/lambda", "/tmp", true);
        }
    }

    @ResponseBody
    public AdminApiPageDataStandardResponse<IndexResponse> index() throws SQLException {
        List<String> tips = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            tips.add(I18nUtil.getBackendStringFromRes("admin.index.welcomeTips_" + i));
        }
        Collections.shuffle(tips);
        ExecutorService executor = ThreadUtils.newFixedThreadPool(20);
        try {
            List<CompletableFuture<?>> futures = new ArrayList<>();
            CompletableFuture<StatisticsInfoResponse> statisticsInfo = new AdminStatisticsService().statisticsInfo(executor);
            futures.add(statisticsInfo);
            CompletableFuture<List<ArticleActivityData>> dataList = new AdminArticleService().activityDataList(executor);
            futures.add(dataList);
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            return new AdminApiPageDataStandardResponse<>(new IndexResponse(statisticsInfo.join(),
                    I18nUtil.getBackendStringFromRes("admin.index.welcomeTip"),
                    new ArrayList<>(Collections.singletonList(tips.get(0))),
                    dataList.join(), BlogBuildInfoUtil.getVersionInfo()));
        } finally {
            executor.shutdown();
        }
    }
}
