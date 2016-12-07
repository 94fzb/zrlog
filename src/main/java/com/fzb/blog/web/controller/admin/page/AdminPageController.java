package com.fzb.blog.web.controller.admin.page;

import com.fzb.blog.model.Comment;
import com.fzb.blog.model.Log;
import com.fzb.blog.model.User;
import com.fzb.blog.util.ParseUtil;
import com.fzb.blog.web.controller.BaseController;
import com.fzb.blog.web.controller.admin.api.UpgradeController;
import com.fzb.blog.web.incp.AdminTokenThreadLocal;

import javax.servlet.http.Cookie;
import java.util.List;
import java.util.Map;

public class AdminPageController extends BaseController {

    public String index() {
        if (AdminTokenThreadLocal.getUser() != null) {
            Map<String, Object> commentMap = Comment.dao.noRead(1, 5);
            if (commentMap.get("rows") != null) {
                List<Comment> rows = (List<Comment>) commentMap.get("rows");
                for (Comment comment : rows) {
                    comment.put("userComment", ParseUtil.autoDigest(comment.get("userComment").toString(), 15));
                }
            }
            getSession().setAttribute("comments", commentMap);
            getSession().setAttribute("commCount", Comment.dao.getCommentCount());
            getSession().setAttribute("toDayCommCount", Comment.dao.getToDayCommentCount());
            getSession().setAttribute("clickCount", Log.dao.getAllClick());
            getSession().setAttribute("lastVersion", new UpgradeController().lastVersion());
            if (getPara(0) == null) {
                return "/admin/index";
            } else {
                return "/admin/" + getPara(0);
            }
        } else {
            return "/admin/login";
        }
    }

    public String login() {
        if (AdminTokenThreadLocal.getUser() != null) {
            return "/admin/index";
        } else {
            return "/admin/login";
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
        redirect("/admin/login");
    }
}
