package com.fzb.blog.web.config;

import com.fzb.blog.web.controller.admin.api.*;
import com.fzb.blog.web.controller.admin.page.AdminArticlePageController;
import com.fzb.blog.web.controller.admin.page.AdminPageController;
import com.fzb.blog.web.controller.admin.page.AdminTemplatePageController;
import com.jfinal.config.Routes;

/**
 * @author zhengchangchun 后台管理者路由
 */
class AdminRoutes extends Routes {
    public void config() {
        add("/admin", AdminPageController.class);
        add("/admin/template", AdminTemplatePageController.class);
        add("/admin/article", AdminArticlePageController.class);
        add("/api/admin", AdminController.class);
        add("/api/admin/link", LinkController.class);
        add("/api/admin/comment", CommentController.class);
        add("/api/admin/tag", TagController.class);
        add("/api/admin/type", TypeController.class);
        add("/api/admin/nav", BlogNavController.class);
        add("/api/admin/article", ArticleController.class);
        add("/api/admin/website", WebSiteController.class);
        add("/api/admin/template", TemplateController.class);
        add("/api/admin/upload", UploadController.class);
        add("/api/admin/upgrade", UpgradeController.class);
    }
}
