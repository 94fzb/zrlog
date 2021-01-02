package com.zrlog.admin.web.controller.api;

import com.hibegin.common.util.BeanUtil;
import com.hibegin.common.util.StringUtils;
import com.jfinal.core.Controller;
import com.zrlog.admin.business.rest.request.UpdateAdminRequest;
import com.zrlog.admin.business.rest.request.UpdatePasswordRequest;
import com.zrlog.admin.business.rest.response.UpdateRecordResponse;
import com.zrlog.admin.business.rest.response.UserBasicInfoResponse;
import com.zrlog.admin.business.service.UserService;
import com.zrlog.admin.web.annotation.RefreshCache;
import com.zrlog.admin.web.token.AdminTokenThreadLocal;
import com.zrlog.model.User;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.ZrLogUtil;

public class AdminUserController extends Controller {

    private final UserService userService = new UserService();

    public UserBasicInfoResponse basicInfo() {
        User byId = new User().findById(AdminTokenThreadLocal.getUserId());
        return BeanUtil.convert(byId.getAttrs(), UserBasicInfoResponse.class);
    }

    @RefreshCache
    public UpdateRecordResponse update() {
        UpdateAdminRequest updateAdminRequest = ZrLogUtil.convertRequestBody(getRequest(), UpdateAdminRequest.class);
        UpdateRecordResponse updateRecordResponse = new UpdateRecordResponse();
        if (updateAdminRequest != null) {
            if (StringUtils.isEmpty(updateAdminRequest.getUserName())) {
                updateRecordResponse.setError(1);
            } else {
                userService.update(AdminTokenThreadLocal.getUserId(), updateAdminRequest);
                updateRecordResponse.setMessage(I18nUtil.getStringFromRes("updatePersonInfoSuccess"));
            }
        } else {
            updateRecordResponse.setError(1);
        }
        return updateRecordResponse;
    }

    public UpdateRecordResponse updatePassword() {
        return userService.updatePassword(AdminTokenThreadLocal.getUserId(),
                ZrLogUtil.convertRequestBody(getRequest(), UpdatePasswordRequest.class));
    }
}
