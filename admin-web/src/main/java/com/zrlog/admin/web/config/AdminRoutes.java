package com.zrlog.admin.web.config;

import com.jfinal.config.Routes;
import com.zrlog.admin.web.controller.api.*;
import com.zrlog.admin.web.controller.page.AdminPageController;
import com.zrlog.admin.web.controller.page.AdminTemplatePageController;
import com.zrlog.common.Constants;

/**
 * 后台管理者路由，这里目前分为2中情况，及服务端响应模板页面和用户对数据的操作
 * 约定
 * 1. 添加页面需要响应时，使用 /admin 的路由，及同步操作
 * 2. 浏览数据，变更数据。使用 /api/admin 的路由，以JSON的格式进行响应，及异步操作
 */
public class AdminRoutes extends Routes {
    @Override
    public void config() {
        add(Constants.ADMIN_URI_BASE_PATH, AdminPageController.class);
        add(Constants.ADMIN_URI_BASE_PATH + "/template", AdminTemplatePageController.class);
        add("/api" + Constants.ADMIN_URI_BASE_PATH, AdminController.class);
        add("/api" + Constants.ADMIN_URI_BASE_PATH + "/link", LinkController.class);
        add("/api" + Constants.ADMIN_URI_BASE_PATH + "/comment", CommentController.class);
        add("/api" + Constants.ADMIN_URI_BASE_PATH + "/type", TypeController.class);
        add("/api" + Constants.ADMIN_URI_BASE_PATH + "/nav", BlogNavController.class);
        add("/api" + Constants.ADMIN_URI_BASE_PATH + "/article", AdminArticleController.class);
        add("/api" + Constants.ADMIN_URI_BASE_PATH + "/website", WebSiteController.class);
        add("/api" + Constants.ADMIN_URI_BASE_PATH + "/template", TemplateController.class);
        add("/api" + Constants.ADMIN_URI_BASE_PATH + "/upload", UploadController.class);
        add("/api" + Constants.ADMIN_URI_BASE_PATH + "/upgrade", UpgradeController.class);
        add("/api" + Constants.ADMIN_URI_BASE_PATH + "/user", AdminUserController.class);
    }
}
