package com.zrlog.admin.business.service;

import com.hibegin.common.util.BeanUtil;
import com.hibegin.common.util.ObjectUtil;
import com.hibegin.common.util.SecurityUtils;
import com.hibegin.common.util.StringUtils;
import com.hibegin.http.server.api.HttpRequest;
import com.zrlog.admin.business.dto.UserLoginDTO;
import com.zrlog.admin.business.exception.*;
import com.zrlog.admin.business.rest.request.LoginRequest;
import com.zrlog.admin.business.rest.request.UpdateAdminRequest;
import com.zrlog.admin.business.rest.request.UpdatePasswordRequest;
import com.zrlog.admin.business.rest.response.UpdateRecordResponse;
import com.zrlog.admin.business.rest.response.UserBasicInfoResponse;
import com.zrlog.business.plugin.UpdateVersionInfoPlugin;
import com.zrlog.blog.web.util.WebTools;
import com.zrlog.common.Constants;
import com.zrlog.model.User;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.ZrLogUtil;

import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class UserService {

    private final UpgradeService upgradeService;

    public UserService() {
        this.upgradeService = new UpgradeService();
    }

    public UpdateRecordResponse updatePassword(int currentUserId, UpdatePasswordRequest updatePasswordRequest) throws SQLException {
        if (ZrLogUtil.isPreviewMode()) {
            throw new PermissionErrorException();
        }
        if (Objects.isNull(updatePasswordRequest)) {
            return new UpdateRecordResponse(false);
        }
        if (StringUtils.isNotEmpty(updatePasswordRequest.getOldPassword()) && StringUtils.isNotEmpty(updatePasswordRequest.getNewPassword())) {
            String dbPassword = new User().getPasswordByUserId(currentUserId);
            String oldPassword = updatePasswordRequest.getOldPassword();
            // compare oldPassword
            if (SecurityUtils.md5(oldPassword).equals(dbPassword)) {
                new User().updatePassword(currentUserId, SecurityUtils.md5(updatePasswordRequest.getNewPassword()));
                UpdateRecordResponse updateRecordResponse = new UpdateRecordResponse();
                updateRecordResponse.setMessage(I18nUtil.getBackendStringFromRes("changePasswordSuccess"));
                return updateRecordResponse;
            } else {
                throw new OldPasswordException();
            }
        } else {
            throw new ArgsException();
        }
    }

    public UserBasicInfoResponse getUserInfo(int userId, String sessionId, HttpRequest request) {
        Map<String, Object> byId = new User().loadById(userId);
        return getUserInfoByUser(byId, sessionId, request);
    }

    private UserBasicInfoResponse getUserInfoByUser(Map<String, Object> byId, String sessionId, HttpRequest request) {
        UserBasicInfoResponse basicInfoResponse = ObjectUtil.requireNonNullElse(BeanUtil.convert(byId, UserBasicInfoResponse.class), new UserBasicInfoResponse());
        if (StringUtils.isEmpty(basicInfoResponse.getHeader())) {
            basicInfoResponse.setHeader(WebTools.buildEncodedUrl(request, "assets/images/default-portrait.gif"));
        }
        UpdateVersionInfoPlugin plugin = (UpdateVersionInfoPlugin) Constants.zrLogConfig.getPlugins().stream().filter(x -> x instanceof UpdateVersionInfoPlugin).findFirst().orElse(null);
        basicInfoResponse.setLastVersion(upgradeService.getCheckVersionResponse(false, plugin));
        basicInfoResponse.setKey(sessionId);
        basicInfoResponse.setCacheableApiUris(Constants.zrLogConfig.getAdminResource().getAdminCacheableApiUris());
        return basicInfoResponse;
    }

    public UserLoginDTO login(LoginRequest loginRequest, HttpRequest request) throws SQLException {
        if (StringUtils.isNotEmpty(loginRequest.getUserName()) && StringUtils.isNotEmpty(loginRequest.getPassword())) {
            Map<String, Object> user = new User().getUserByUserName(loginRequest.getUserName().toLowerCase());
            if (Objects.isNull(user)) {
                throw new UserNameOrPasswordException();
            }
            String dbPassword = (String) user.get("password");
            if (dbPassword == null || !Objects.equals(dbPassword.toLowerCase(), loginRequest.getPassword().toLowerCase())) {
                throw new UserNameOrPasswordException();
            }
            UserBasicInfoResponse userInfoByUser = getUserInfoByUser(user, UUID.randomUUID().toString(), request);
            UserLoginDTO  userLoginDTO = new UserLoginDTO();
            userLoginDTO.setSecretKey((String) user.get("secretKey"));
            userLoginDTO.setUserBasicInfoResponse(userInfoByUser);
            userLoginDTO.setId((Integer) user.get("userId"));
            return userLoginDTO;
        } else {
            throw new UserNameAndPasswordRequiredException();
        }
    }


    public Object update(int userId, UpdateAdminRequest updateAdminRequest) throws SQLException {
        if (ZrLogUtil.isPreviewMode()) {
            throw new PermissionErrorException();
        }
        new User().updateEmailUserNameHeaderByUserId(updateAdminRequest.getEmail(), updateAdminRequest.getUserName(), updateAdminRequest.getHeader(), userId);
        return new User().loadById(userId);
    }
}
