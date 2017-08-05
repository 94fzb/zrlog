package com.fzb.blog.web.controller.admin.api;

import com.fzb.blog.common.response.LoginResponse;
import com.fzb.blog.common.response.UpdateRecordResponse;
import com.fzb.blog.model.User;
import com.fzb.blog.util.I18NUtil;
import com.fzb.blog.web.controller.BaseController;
import com.fzb.blog.web.token.AdminTokenService;
import com.fzb.blog.web.token.AdminTokenThreadLocal;
import com.fzb.common.util.SecurityUtils;
import com.jfinal.plugin.activerecord.Db;
import org.apache.commons.lang3.StringUtils;

public class AdminController extends BaseController {

    private AdminTokenService adminTokenService = new AdminTokenService();

    public LoginResponse login() {
        String key = getPara("key");
        String password = getPara("password");
        String userName = getPara("userName");
        LoginResponse loginResponse = new LoginResponse();
        if (StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(password) && StringUtils.isNotBlank(key)) {
            String dbPassword = User.dao.getPasswordByUserName(userName.toLowerCase());
            if (dbPassword != null && SecurityUtils.md5(key + ":" + dbPassword).equals(password)) {
                adminTokenService.setAdminToken(User.dao.getIdByUserName(userName.toLowerCase()), getRequest(), getResponse());
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
