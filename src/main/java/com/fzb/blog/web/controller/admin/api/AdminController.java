package com.fzb.blog.web.controller.admin.api;

import com.fzb.blog.common.response.LoginResponse;
import com.fzb.blog.common.response.UpdateRecordResponse;
import com.fzb.blog.model.User;
import com.fzb.blog.util.ResUtil;
import com.fzb.blog.web.controller.BaseController;
import com.fzb.blog.web.util.WebTools;
import com.fzb.common.util.Md5Util;
import com.jfinal.plugin.activerecord.Db;

import javax.servlet.http.Cookie;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AdminController extends BaseController {

    private String getBaseMs() {
        return Md5Util.MD5(WebTools.getRealIp(getRequest()) + "," + getRequest().getHeader("User-Agent")).substring(2, 10);
    }

    public LoginResponse login() {
        boolean login = false;
        LoginResponse loginResponse = new LoginResponse();
        if (getCookie("zId") != null) {
            Map<String, User> userMap = (Map<String, User>) getSession().getServletContext().getAttribute("userMap");
            if (userMap != null) {
                String zId = getBaseMs() + getCookie("zId");
                User user = userMap.get(zId);
                if (user != null) {
                    user = User.dao.login(user.getStr("userName").toLowerCase(), user.getStr("password"));
                    if (user != null) {
                        getSession().setAttribute("user", user);
                        login = true;
                    }
                }
            }
        }
        if (!login && getPara("userName") != null && getPara("password") != null) {
            User user = User.dao.login(getPara("userName").toLowerCase(),
                    Md5Util.MD5(getPara("password")));
            if (user != null) {
                getSession().setAttribute("user", user);
                if ("on".equals(getPara("rememberMe"))) {
                    Map<String, User> userMap = (Map<String, User>) getSession().getServletContext().getAttribute("userMap");
                    if (userMap == null) {
                        userMap = new HashMap<String, User>();
                        getSession().getServletContext().setAttribute("userMap", userMap);
                    }
                    String zid = UUID.randomUUID().toString();
                    Cookie cookie = new Cookie("zId", zid);
                    cookie.setMaxAge(60 * 60 * 24 * 30);
                    //cookie.setHttpOnly(true);
                    String host = getRequest().getHeader("Host");
                    int idx = host.indexOf(":");
                    if (idx != -1) {
                        host = host.substring(0, idx);
                    }
                    cookie.setDomain(host);
                    cookie.setPath("/");
                    getResponse().addCookie(cookie);
                    userMap.put(getBaseMs() + zid, user);
                }
            } else {
                loginResponse.setError(1);
                loginResponse.setMessage(ResUtil.getStringFromRes("userNameOrPasswordError", getRequest()));
            }
        }
        return loginResponse;
    }

    public UpdateRecordResponse update() {
        UpdateRecordResponse updateRecordResponse = new UpdateRecordResponse();
        User user = (User) getSession().getAttribute("user");
        Integer userId = user.getInt("userId");
        Db.update("update user set header=?,email=?,userName=? where userId=?", getPara("header"), getPara("email"), getPara("userName"), userId);
        getSession().setAttribute("user", User.dao.findById(userId));
        updateRecordResponse.setMessage(ResUtil.getStringFromRes("updatePersonInfoSuccess", getRequest()));
        return updateRecordResponse;
    }

    public UpdateRecordResponse changePassword() {
        UpdateRecordResponse updateRecordResponse = new UpdateRecordResponse();
        if (isNotNullOrNotEmptyStr(getPara("oldPassword"), getPara("newPassword"))) {
            String userName = ((User) getSessionAttr("user")).getStr("userName");
            String dbPassword = User.dao.getPasswordByName(userName);
            String oldPassword = getPara("oldPassword");
            // compare oldPassword
            if (Md5Util.MD5(oldPassword).equals(dbPassword)) {
                User.dao.updatePassword(userName, Md5Util.MD5(getPara("newPassword")));
                updateRecordResponse.setMessage(ResUtil.getStringFromRes("changePasswordSuccess", getRequest()));
                getSession().invalidate();
            } else {
                updateRecordResponse.setError(1);
                updateRecordResponse.setMessage(ResUtil.getStringFromRes("oldPasswordError", getRequest()));
            }
        } else {
            updateRecordResponse.setError(1);
            updateRecordResponse.setMessage(ResUtil.getStringFromRes("argsError", getRequest()));
        }
        return updateRecordResponse;
    }
}
