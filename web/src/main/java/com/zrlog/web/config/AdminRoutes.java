package com.zrlog.web.config;

import com.jfinal.config.Routes;
import com.zrlog.web.controller.admin.api.*;
import com.zrlog.web.controller.admin.page.AdminArticlePageController;
import com.zrlog.web.controller.admin.page.AdminPageController;
import com.zrlog.web.controller.admin.page.AdminTemplatePageController;

/**
 * 后台管理者路由，这里目前分为2中情况，及服务端响应模板页面和用户对数据的操作
 * 约定
 * 1. 添加页面需要响应时，使用 /admin 的路由，及同步操作
 * 2. 浏览数据，变更数据。使用 /api/admin 的路由，以JSON的格式进行响应，及异步操作
 */
class AdminRoutes extends Routes {
    @Override
    public void config() {
        add("/admin", AdminPageController.class);
        add("/admin/template", AdminTemplatePageController.class);
        add("/admin/article", AdminArticlePageController.class);
        add("/api/admin", AdminController.class);
        add("/api/admin/link", LinkController.class);
        add("/api/admin/comment", CommentController.class);
        add("/api/admin/type", TypeController.class);
        add("/api/admin/nav", BlogNavController.class);
        add("/api/admin/article", ArticleController.class);
        add("/api/admin/website", WebSiteController.class);
        add("/api/admin/template", TemplateController.class);
        add("/api/admin/upload", UploadController.class);
        add("/api/admin/upgrade", UpgradeController.class);
    }
}
