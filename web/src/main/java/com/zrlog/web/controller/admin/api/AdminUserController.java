package com.zrlog.web.controller.admin.api;

import com.hibegin.common.util.BeanUtil;
import com.hibegin.common.util.StringUtils;
import com.zrlog.business.rest.request.UpdateAdminRequest;
import com.zrlog.business.rest.request.UpdatePasswordRequest;
import com.zrlog.business.rest.response.UpdateRecordResponse;
import com.zrlog.business.rest.response.UserBasicInfoResponse;
import com.zrlog.business.service.UserService;
import com.zrlog.model.User;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.ZrLogUtil;
import com.zrlog.web.annotation.RefreshCache;
import com.zrlog.web.controller.BaseController;
import com.zrlog.web.token.AdminTokenThreadLocal;

public class AdminUserController extends BaseController {

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
        return userService.updatePassword(AdminTokenThreadLocal.getUserId(), ZrLogUtil.convertRequestBody(getRequest(), UpdatePasswordRequest.class));
    }
}
