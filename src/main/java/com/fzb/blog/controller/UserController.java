package com.fzb.blog.controller;

import com.fzb.blog.model.Comment;
import com.fzb.blog.model.Link;
import com.fzb.blog.model.Log;
import com.fzb.blog.model.User;
import com.fzb.blog.util.ResUtil;
import com.fzb.blog.util.WebTools;
import com.fzb.common.util.Md5Util;
import com.fzb.common.util.ParseTools;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.Db;

import javax.servlet.http.Cookie;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UserController extends ManageController {
    public void delete() {
        Link.dao.deleteById(getPara(0));
    }

    public void queryAll() {

    }

    public void index() {
        if (getSessionAttr("user") != null) {
            Map<String, Object> commentMap = Comment.dao.noRead(1, 5);
            if (commentMap.get("rows") != null) {
                List<Comment> rows = (List<Comment>) commentMap.get("rows");
                for (Comment comment : rows) {
                    comment.put("userComment", ParseTools.autoDigest(comment.get("userComment").toString(), 15));
                }
            }
            getSession().setAttribute("comments", commentMap);
            getSession().setAttribute("commCount", Comment.dao.getCommentCount());
            getSession().setAttribute("toDayCommCount", Comment.dao.getToDayCommentCount());
            getSession().setAttribute("clickCount", Log.dao.getAllClick());
            if (getPara("redirectFrom") != null
                    && !"".equals(getPara("redirectFrom"))) {
                redirect(getPara("redirectFrom"));
                return;
            }
            if (getPara(0) == null) {
                render("/admin/index.jsp");
            } else {
                String jsp = "/admin/" + getPara(0) + ".jsp";
                File file = new File(PathKit.getWebRootPath() + jsp);
                if (file.exists()) {
                    render(jsp);
                } else {
                    renderNotPage();
                }
            }
        } else {
            render("/admin/login.jsp");
        }

    }

    public void logout() {
        Cookie cookies[] = getRequest().getCookies();
        for (Cookie cookie : cookies) {
            if ("zId".equals(cookie.getName())) {
                Map<String, User> userMap = (Map<String, User>) getSession().getServletContext().getAttribute("userMap");
                if (userMap != null) {
                    userMap.remove(cookie.getValue());
                    cookie.setMaxAge(0);
                    getResponse().addCookie(cookie);
                }
            }
        }
        getSession().invalidate();
        render("/admin/login.jsp");
    }

    private String getBaseMs() {
        return Md5Util.MD5(WebTools.getRealIp(getRequest()) + "," + getRequest().getHeader("User-Agent")).substring(2, 10);
    }

    public void login() {
        boolean login = false;
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
                setAttr("errorMsg", ResUtil.getStringFromRes("userNameOrPasswordError", getRequest()));
            }
        }
        index();
    }

    @Override
    public void add() {

    }

    @Override
    public void update() {
        Db.update("update user set header=?,email=? where userName=?", getPara("header"), getPara("email"), getPara("userName"));
        setAttr("message", ResUtil.getStringFromRes("updatePersonInfoSuccess", getRequest()));
    }

    public void changePassword() {
        if (isNotNullOrNotEmptyStr(getPara("oldPassword"), getPara("newPassword"))) {
            String userName = ((User) getSessionAttr("user")).getStr("userName");
            String dbPassword = User.dao.getPasswordByName(userName);
            String oldPassword = getPara("oldPassword");
            // compare oldPassword
            if (Md5Util.MD5(oldPassword).equals(dbPassword)) {
                User.dao.updatePassword(userName, Md5Util.MD5(getPara("newPassword")));
                setAttr("message", ResUtil.getStringFromRes("changePasswordSuccess", getRequest()));
                getSession().invalidate();
            } else {
                setAttr("message", ResUtil.getStringFromRes("oldPasswordError", getRequest()));
            }
        } else {
            setAttr("message", ResUtil.getStringFromRes("argsError", getRequest()));
        }
    }
}
