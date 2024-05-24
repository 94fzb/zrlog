package com.zrlog.admin.web.controller.api;

import com.hibegin.common.util.BeanUtil;
import com.hibegin.common.util.FileUtils;
import com.hibegin.common.util.LoggerUtil;
import com.hibegin.http.annotation.ResponseBody;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.hibegin.http.server.util.PathUtil;
import com.hibegin.http.server.web.Controller;
import com.zrlog.admin.business.rest.request.LoginRequest;
import com.zrlog.admin.business.rest.response.IndexResponse;
import com.zrlog.admin.business.rest.response.LoginResponse;
import com.zrlog.admin.business.rest.response.StatisticsInfoResponse;
import com.zrlog.admin.business.rest.response.UpdateRecordResponse;
import com.zrlog.admin.business.service.UserService;
import com.zrlog.business.util.InstallUtils;
import com.zrlog.common.Constants;
import com.zrlog.common.rest.response.ApiStandardResponse;
import com.zrlog.common.vo.ServerInfo;
import com.zrlog.model.Comment;
import com.zrlog.model.Log;
import com.zrlog.model.User;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.ServerInfoUtils;
import com.zrlog.util.ZrLogUtil;

import java.io.File;
import java.sql.SQLException;
import java.util.*;

public class AdminController extends Controller {

    private final UserService userService = new UserService();


    public AdminController() {
    }

    public AdminController(HttpRequest request, HttpResponse response) {
        super(request, response);
    }

    @ResponseBody
    public ApiStandardResponse<LoginResponse> login() throws SQLException {
        LoginRequest loginRequest = BeanUtil.convertWithValid(getRequest().getInputStream(), LoginRequest.class);
        userService.login(loginRequest);
        String key = UUID.randomUUID().toString();
        Constants.zrLogConfig.getTokenService().setAdminToken(new User().getUserByUserName(loginRequest.getUserName().toLowerCase()), key, Objects.equals(loginRequest.getHttps(), true) ? "https" : "http", getRequest(), getResponse());
        return new ApiStandardResponse<>(new LoginResponse(key));
    }

    /**
     * 触发更新缓存
     */
    @ResponseBody
    public UpdateRecordResponse refreshCache() {
        Constants.zrLogConfig.getCacheService().refreshInitDataCacheAsync(request, true);
        return new UpdateRecordResponse();
    }

    private ApiStandardResponse<List<ServerInfo>> serverInfo() {
        Map<String, Object> info = new HashMap<>();
        InstallUtils.getSystemProp().forEach((key, value) -> info.put(key.toString(), value));
        BlogBuildInfoUtil.getBlogProp().forEach((key, value) -> info.put("zrlog." + key.toString(), value));
        return new ApiStandardResponse<>(ServerInfoUtils.convertToServerInfos(info));
    }

    private ApiStandardResponse<StatisticsInfoResponse> statisticsInfo() throws SQLException {
        StatisticsInfoResponse info = new StatisticsInfoResponse();
        info.setCommCount(new Comment().count());
        info.setToDayCommCount(new Comment().countToDayComment());
        info.setClickCount(new Log().sumClick().longValue());
        info.setArticleCount(new Log().getAdminCount());
        List<File> allFileList = new ArrayList<>();
        try {
            List<String> baseFolders = new ArrayList<>(Arrays.asList(PathUtil.getRootPath() + "/bin", PathUtil.getTempPath(),
                    PathUtil.getLogPath(), PathUtil.getConfPath(), PathUtil.getStaticPath(), PathUtil.getRootPath() + "/lib"));
            allFileList.add(new File(PathUtil.getRootPath() + "/" + Constants.zrLogConfig.getJarUpdater().fileName()));
            for (String folder : baseFolders) {
                FileUtils.getAllFiles(folder, allFileList);
            }
            List<File> cacheFileList = new ArrayList<>();
            FileUtils.getAllFiles(PathUtil.getCachePath(), cacheFileList);
            allFileList.addAll(cacheFileList);
            info.setUsedDiskSpace(allFileList.stream().mapToLong(File::length).sum());
            info.setUsedCacheSpace(cacheFileList.stream().mapToLong(File::length).sum());
        } catch (Exception e) {
            LoggerUtil.getLogger(AdminController.class).warning("Load used disk info error " + e.getMessage());
            info.setUsedCacheSpace(-1L);
            info.setUsedDiskSpace(-1L);
        }
        return new ApiStandardResponse<>(info);
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
        Constants.DEV_MODE = true;
        return new ApiStandardResponse<>();
    }

    @ResponseBody
    public ApiStandardResponse<IndexResponse> index() throws SQLException {
        List<String> tips = new ArrayList<>(Arrays.asList(I18nUtil.getBackendStringFromRes("admin.index.welcomeTips_1"),
                I18nUtil.getBackendStringFromRes("admin.index.welcomeTips_2"), I18nUtil.getBackendStringFromRes("admin.index.welcomeTips_3")));
        Collections.shuffle(tips);
        return new ApiStandardResponse<>(new IndexResponse(statisticsInfo().getData(), serverInfo().getData(),
                new ArrayList<>(Collections.singletonList(tips.getFirst())), ZrLogUtil.isDockerMode()));
    }
}
