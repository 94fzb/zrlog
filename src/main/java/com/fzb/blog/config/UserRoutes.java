package com.fzb.blog.config;

import com.fzb.blog.controller.*;
import com.jfinal.config.Routes;

/**
 * @author zhengchangchun 后台管理者路由
 */
public class UserRoutes extends Routes {
    public void config() {
        add("/admin", UserController.class);
        add("/admin/link", LinkController.class);
        add("/admin/comment", CommentController.class);
        add("/admin/tag", TagController.class);
        add("/admin/plugin", PluginController.class);
        add("/admin/type", TypeController.class);
        add("/admin/nav", LogNavController.class);
        add("/admin/log", ManageLogController.class);
        add("/admin/website", WebSiteController.class);
        add("/admin/template", TemplateController.class);
    }
}
