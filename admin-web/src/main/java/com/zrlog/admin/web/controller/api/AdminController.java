package com.zrlog.admin.web.controller.api;

import com.google.gson.Gson;
import com.hibegin.common.util.BeanUtil;
import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.ObjectHelpers;
import com.hibegin.common.util.StringUtils;
import com.hibegin.http.annotation.ResponseBody;
import com.hibegin.http.server.web.Controller;
import com.zrlog.admin.business.dto.UserLoginDTO;
import com.zrlog.admin.business.rest.request.LoginRequest;
import com.zrlog.admin.business.rest.response.*;
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
import com.zrlog.util.ThreadUtils;
import com.zrlog.util.ZrLogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminController extends Controller {

    private final UserService userService = new UserService();

    @ResponseBody
    public ApiStandardResponse<UserBasicInfoResponse> login() throws Exception {
        if (!InstallUtils.isInstalled()) {
            throw new MissingInstallException();
        }
        LoginRequest loginRequest = BeanUtil.convertWithValid(getRequest().getInputStream(), LoginRequest.class);
        UserLoginDTO dto = userService.login(loginRequest, request);
        Constants.zrLogConfig.getTokenService().setAdminToken(dto.getId(), dto.getSecretKey(), dto.getUserBasicInfoResponse().getKey(),
                Objects.equals(loginRequest.getHttps(), true) ? "https" : "http", getRequest(), getResponse());
        return new ApiStandardResponse<>(dto.getUserBasicInfoResponse());
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
            map.put("name", Constants.getAdminDocumentTitleByUri("/"));
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

    private CompletableFuture<StatisticsInfoResponse> statisticsInfo(Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            StatisticsInfoResponse info = new StatisticsInfoResponse();
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    info.setCommCount(new Comment().count());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }, executor));
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    info.setToDayCommCount(new Comment().countToDayComment());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }, executor));
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    info.setClickCount(new Log().sumClick().longValue());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }, executor));
            futures.add(CompletableFuture.runAsync(() -> {
                info.setArticleCount(new Log().getAdminCount());
            }, executor));
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            return info;
        }, executor);
    }

    @ResponseBody
    public ApiStandardResponse<Map<String, Object>> error() {
        Map<String, Object> map = new HashMap<>();
        map.put("message", request.getParaToStr("message", ""));
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
        ExecutorService executor = ThreadUtils.newFixedThreadPool(20);
        try {
            List<CompletableFuture> futures = new ArrayList<>();
            CompletableFuture<StatisticsInfoResponse> statisticsInfo = statisticsInfo(executor);
            futures.add(statisticsInfo);
            CompletableFuture<List<ArticleActivityData>> dataList = new AdminArticleService().activityDataList(executor);
            futures.add(dataList);
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            return new ApiStandardResponse<>(new IndexResponse(statisticsInfo.join(),
                    I18nUtil.getBackendStringFromRes("admin.index.welcomeTip"),
                    new ArrayList<>(Collections.singletonList(tips.get(0))),
                    dataList.join(), BlogBuildInfoUtil.getVersionInfo()));
        } finally {
            executor.shutdown();
        }
    }
}
