package com.zrlog.web.controller.admin.api;

import com.hibegin.common.util.StringUtils;
import com.zrlog.common.request.LoginRequest;
import com.zrlog.common.response.LoginResponse;
import com.zrlog.common.response.UpdateRecordResponse;
import com.zrlog.util.I18NUtil;
import com.zrlog.util.ZrlogUtil;
import com.zrlog.web.token.AdminTokenThreadLocal;
import com.zrlog.model.User;
import com.zrlog.web.controller.BaseController;
import com.zrlog.web.token.AdminTokenService;
import com.hibegin.common.util.SecurityUtils;
import com.jfinal.plugin.activerecord.Db;

import java.util.concurrent.atomic.AtomicInteger;

public class AdminController extends BaseController {

    private AdminTokenService adminTokenService = new AdminTokenService();

    private static AtomicInteger sessionAtomicInteger = new AtomicInteger();

    public LoginResponse login() {
        LoginRequest loginRequest = ZrlogUtil.convertRequestBody(getRequest(), LoginRequest.class);
        LoginResponse loginResponse = new LoginResponse();
        if (loginRequest != null && StringUtils.isNotEmpty(loginRequest.getUserName()) &&
                StringUtils.isNotEmpty(loginRequest.getPassword()) && StringUtils.isNotEmpty(loginRequest.getKey())) {
            String dbPassword = User.dao.getPasswordByUserName(loginRequest.getUserName().toLowerCase());
            if (dbPassword != null && SecurityUtils.md5(loginRequest.getKey() + ":" + dbPassword).equals(loginRequest.getPassword())) {
                adminTokenService.setAdminToken(User.dao.getIdByUserName(loginRequest.getUserName().toLowerCase()), sessionAtomicInteger.incrementAndGet(), getRequest(), getResponse());
            } else {
                loginResponse.setError(1);
                loginResponse.setMessage(I18NUtil.getStringFromRes("userNameOrPasswordError", getRequest()));
            }
        }
        return loginResponse;
    }

    public UpdateRecordResponse update() {
        UpdateRecordResponse updateRecordResponse = new UpdateRecordResponse();
        Integer userId = AdminTokenThreadLocal.getUserId();
        Db.update("update user set header=?,email=?,userName=? where userId=?", getPara("header"), getPara("email"), getPara("userName"), userId);
        getRequest().setAttribute("user", User.dao.findById(userId));
        updateRecordResponse.setMessage(I18NUtil.getStringFromRes("updatePersonInfoSuccess", getRequest()));
        return updateRecordResponse;
    }

    public UpdateRecordResponse changePassword() {
        UpdateRecordResponse updateRecordResponse = new UpdateRecordResponse();
        if (isNotNullOrNotEmptyStr(getPara("oldPassword"), getPara("newPassword"))) {
            String dbPassword = User.dao.getPasswordByUserId(AdminTokenThreadLocal.getUserId());
            String oldPassword = getPara("oldPassword");
            // compare oldPassword
            if (SecurityUtils.md5(oldPassword).equals(dbPassword)) {
                User.dao.updatePassword(AdminTokenThreadLocal.getUserId(), SecurityUtils.md5(getPara("newPassword")));
                updateRecordResponse.setMessage(I18NUtil.getStringFromRes("changePasswordSuccess", getRequest()));
                getSession().invalidate();
            } else {
                updateRecordResponse.setError(1);
                updateRecordResponse.setMessage(I18NUtil.getStringFromRes("oldPasswordError", getRequest()));
            }
        } else {
            updateRecordResponse.setError(1);
            updateRecordResponse.setMessage(I18NUtil.getStringFromRes("argsError", getRequest()));
        }
        return updateRecordResponse;
    }
}
