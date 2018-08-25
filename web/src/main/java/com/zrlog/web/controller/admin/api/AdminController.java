package com.zrlog.web.controller.admin.api;

import com.hibegin.common.util.StringUtils;
import com.zrlog.common.request.ChangePasswordRequest;
import com.zrlog.common.request.LoginRequest;
import com.zrlog.common.request.UpdateAdminRequest;
import com.zrlog.common.response.LoginResponse;
import com.zrlog.common.response.UpdateRecordResponse;
import com.zrlog.service.AdminTokenThreadLocal;
import com.zrlog.service.UserService;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.ZrLogUtil;
import com.zrlog.web.annotation.RefreshCache;
import com.zrlog.web.controller.BaseController;

public class AdminController extends BaseController {

    private UserService userService = new UserService();

    public LoginResponse login() {
        return userService.login(ZrLogUtil.convertRequestBody(getRequest(), LoginRequest.class), getRequest(), getResponse());
    }

    @RefreshCache
    public UpdateRecordResponse update() {
        UpdateAdminRequest updateAdminRequest = ZrLogUtil.convertRequestBody(getRequest(), UpdateAdminRequest.class);
        UpdateRecordResponse updateRecordResponse = new UpdateRecordResponse();
        if (updateAdminRequest != null) {
            if (StringUtils.isEmpty(updateAdminRequest.getUserName())) {
                updateRecordResponse.setError(1);
            } else {
                if (ZrLogUtil.isPreviewMode() && !System.getenv("DEFAULT_USERNAME").equals(updateAdminRequest.getUserName())) {
                    return errorUpdateRecordResponse();
                } else {
                    updateAdminRequest.setUserId(AdminTokenThreadLocal.getUserId());
                    getRequest().setAttribute("user", userService.update(updateAdminRequest));
                    updateRecordResponse.setMessage(I18nUtil.getStringFromRes("updatePersonInfoSuccess"));
                }
            }
        } else {
            updateRecordResponse.setError(1);
        }
        return updateRecordResponse;
    }

    public UpdateRecordResponse changePassword() {
        if (ZrLogUtil.isPreviewMode()) {
            return errorUpdateRecordResponse();
        } else {
            return userService.updatePassword(ZrLogUtil.convertRequestBody(getRequest(), ChangePasswordRequest.class));
        }
    }

    private UpdateRecordResponse errorUpdateRecordResponse() {
        UpdateRecordResponse updateRecordResponse = new UpdateRecordResponse();
        updateRecordResponse.setError(1);
        updateRecordResponse.setMessage(I18nUtil.getStringFromRes("permissionError"));
        return updateRecordResponse;
    }

    /**
     * 插件调用这个方法
     */
    @RefreshCache
    public UpdateRecordResponse refreshCache() {
        return new UpdateRecordResponse();
    }
}
