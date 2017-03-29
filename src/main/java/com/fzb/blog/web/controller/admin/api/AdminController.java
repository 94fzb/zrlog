package com.fzb.blog.web.controller.admin.api;

import com.fzb.blog.common.response.LoginResponse;
import com.fzb.blog.common.response.UpdateRecordResponse;
import com.fzb.blog.model.User;
import com.fzb.blog.util.I18NUtil;
import com.fzb.blog.web.controller.BaseController;
import com.fzb.blog.web.incp.AdminTokenService;
import com.fzb.blog.web.incp.AdminTokenThreadLocal;
import com.fzb.blog.web.util.WebTools;
import com.fzb.common.util.SecurityUtils;
import com.jfinal.core.JFinal;
import com.jfinal.plugin.activerecord.Db;

import javax.servlet.http.Cookie;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AdminController extends BaseController {

    private AdminTokenService adminTokenService = new AdminTokenService();

    private String getBaseMs() {
        return SecurityUtils.md5(WebTools.getRealIp(getRequest()) + "," + getRequest().getHeader("User-Agent")).substring(2, 10);
    }

    public LoginResponse login() {
        boolean login = false;
        LoginResponse loginResponse = new LoginResponse();
        if (getCookie("zId") != null) {
            Map<String, User> userMap = (Map<String, User>) JFinal.me().getServletContext().getAttribute("userMap");
            if (userMap != null) {
                String zId = getBaseMs() + getCookie("zId");
                User user = userMap.get(zId);
                if (user != null) {
                    user = User.dao.login(user.getStr("userName").toLowerCase(), user.getStr("password"));
                    if (user != null) {
                        adminTokenService.setAdminToken(user.getInt("userId"), getRequest(), getResponse());
                        login = true;
                    }
                }
            }
        }
        if (!login && getPara("userName") != null && getPara("password") != null) {
            User user = User.dao.login(getPara("userName").toLowerCase(), SecurityUtils.md5(getPara("password")));
            if (user != null) {
                if ("on".equals(getPara("rememberMe"))) {
                    Map<String, User> userMap = (Map<String, User>) JFinal.me().getServletContext().getAttribute("userMap");
                    if (userMap == null) {
                        userMap = new HashMap<String, User>();
                        JFinal.me().getServletContext().setAttribute("userMap", userMap);
                    }
                    String zid = UUID.randomUUID().toString();
                    Cookie cookie = new Cookie("zId", zid);
                    cookie.setMaxAge(60 * 60 * 24 * 30);
                    //cookie.setHttpOnly(true);
                    String host = getDomain();
                    cookie.setDomain(host);
                    cookie.setPath("/");
                    getResponse().addCookie(cookie);
                    userMap.put(getBaseMs() + zid, user);
                }
                adminTokenService.setAdminToken(user.getInt("userId"), getRequest(), getResponse());
            } else {
                loginResponse.setError(1);
                loginResponse.setMessage(I18NUtil.getStringFromRes("userNameOrPasswordError", getRequest()));
            }
        }
        return loginResponse;
    }


    private String getDomain() {
        String host = getRequest().getHeader("Host");
        int idx = host.indexOf(":");
        if (idx != -1) {
            host = host.substring(0, idx);
        }
        return host;
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
