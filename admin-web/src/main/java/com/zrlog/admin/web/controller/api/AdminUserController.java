package com.zrlog.admin.web.controller.api;

import com.hibegin.common.util.BeanUtil;
import com.hibegin.common.util.StringUtils;
import com.hibegin.http.annotation.ResponseBody;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.hibegin.http.server.web.Controller;
import com.zrlog.admin.business.rest.request.CreateLinkRequest;
import com.zrlog.admin.business.rest.request.UpdateAdminRequest;
import com.zrlog.admin.business.rest.request.UpdatePasswordRequest;
import com.zrlog.admin.business.rest.response.UpdateRecordResponse;
import com.zrlog.admin.business.rest.response.UserBasicInfoResponse;
import com.zrlog.admin.business.service.UpgradeService;
import com.zrlog.admin.business.service.UserService;
import com.zrlog.admin.web.annotation.RefreshCache;
import com.zrlog.admin.web.plugin.UpdateVersionPlugin;
import com.zrlog.admin.web.token.AdminTokenThreadLocal;
import com.zrlog.common.Constants;
import com.zrlog.common.rest.response.ApiStandardResponse;
import com.zrlog.model.User;
import com.zrlog.util.I18nUtil;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.net.URI;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;

public class AdminUserController extends Controller {

    private final UserService userService;
    private final UpgradeService upgradeService;

    public AdminUserController() {
        this.userService = new UserService();
        this.upgradeService = new UpgradeService();
    }

    public AdminUserController(HttpRequest request, HttpResponse response) {
        super(request, response);
        this.userService = new UserService();
        this.upgradeService = new UpgradeService();
    }

    @ResponseBody
    public ApiStandardResponse<UserBasicInfoResponse> index() {
        Map<String, Object> byId = new User().loadById(AdminTokenThreadLocal.getUserId());
        UserBasicInfoResponse basicInfoResponse = Objects.requireNonNullElse(BeanUtil.convert(byId, UserBasicInfoResponse.class), new UserBasicInfoResponse());
        if (StringUtils.isEmpty(basicInfoResponse.getHeader())) {
            basicInfoResponse.setHeader("/assets/images/default-portrait.gif");
        }
        UpdateVersionPlugin plugin = (UpdateVersionPlugin) Constants.zrLogConfig.getPlugins().stream().filter(x -> x instanceof UpdateVersionPlugin).findFirst().orElse(null);
        basicInfoResponse.setLastVersion(upgradeService.getCheckVersionResponse(false, plugin));
        return new ApiStandardResponse<>(basicInfoResponse);
    }

    @RefreshCache
    @ResponseBody
    public UpdateRecordResponse update() throws SQLException {
        UpdateAdminRequest updateAdminRequest = Objects.requireNonNullElse(BeanUtil.convertWithValid(getRequest().getInputStream(), UpdateAdminRequest.class), new UpdateAdminRequest());
        UpdateRecordResponse updateRecordResponse = new UpdateRecordResponse();
        if (StringUtils.isEmpty(updateAdminRequest.getUserName())) {
            updateRecordResponse.setError(1);
        } else {
            userService.update(AdminTokenThreadLocal.getUserId(), updateAdminRequest);
            updateRecordResponse.setMessage(I18nUtil.getBackendStringFromRes("updatePersonInfoSuccess"));
        }
        return updateRecordResponse;
    }

    @ResponseBody
    public UpdateRecordResponse updatePassword() throws SQLException {
        return userService.updatePassword(AdminTokenThreadLocal.getUserId(),
                BeanUtil.convertWithValid(getRequest().getInputStream(), UpdatePasswordRequest.class));
    }
}
