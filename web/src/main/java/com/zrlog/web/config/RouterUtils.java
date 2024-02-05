package com.zrlog.web.config;

import com.hibegin.http.server.web.Router;
import com.zrlog.admin.web.controller.api.*;
import com.zrlog.admin.web.controller.page.AdminPageController;
import com.zrlog.admin.web.controller.page.AdminTemplatePageController;
import com.zrlog.blog.web.controller.api.ApiInstallController;
import com.zrlog.blog.web.controller.api.BlogApiPublicController;
import com.zrlog.blog.web.controller.page.ArticleController;
import com.zrlog.blog.web.controller.page.InstallController;
import com.zrlog.business.util.InstallUtils;
import com.zrlog.common.Constants;

public class RouterUtils {
    /**
     * 后台管理者路由，这里目前分为2中情况，及服务端响应模板页面和用户对数据的操作
     * 约定
     * 1. 添加页面需要响应时，使用 /admin 的路由，及同步操作
     * 2. 浏览数据，变更数据。使用 /api/admin 的路由，以JSON的格式进行响应，及异步操作
     */
    public static void configAdminRoute(Router router) {
        // 后台管理者
        router.addMapper(Constants.ADMIN_URI_BASE_PATH, AdminPageController.class);
        router.addMapper(Constants.ADMIN_URI_BASE_PATH + "/login", AdminPageController.class, "index");
        router.addMapper(Constants.ADMIN_URI_BASE_PATH + "/upgrade", AdminPageController.class, "index");
        router.addMapper(Constants.ADMIN_URI_BASE_PATH + "/article-edit", AdminPageController.class, "index");
        router.addMapper(Constants.ADMIN_URI_BASE_PATH + "/article", AdminPageController.class, "index");
        router.addMapper(Constants.ADMIN_URI_BASE_PATH + "/type", AdminPageController.class, "index");
        router.addMapper(Constants.ADMIN_URI_BASE_PATH + "/link", AdminPageController.class, "index");
        router.addMapper(Constants.ADMIN_URI_BASE_PATH + "/comment", AdminPageController.class, "index");
        router.addMapper(Constants.ADMIN_URI_BASE_PATH + "/plugin", AdminPageController.class, "index");
        router.addMapper(Constants.ADMIN_URI_BASE_PATH + "/website", AdminPageController.class, "index");
        router.addMapper(Constants.ADMIN_URI_BASE_PATH + "/nav", AdminPageController.class, "index");
        router.addMapper(Constants.ADMIN_URI_BASE_PATH + "/template-config", AdminPageController.class, "index");
        router.addMapper(Constants.ADMIN_URI_BASE_PATH + "/user", AdminPageController.class, "index");
        router.addMapper(Constants.ADMIN_URI_BASE_PATH + "/user-update-password", AdminPageController.class, "index");
        router.addMapper(Constants.ADMIN_URI_BASE_PATH + "/template", AdminPageController.class, "index");
        //template download
        router.addMapper(Constants.ADMIN_URI_BASE_PATH + "/template/download", AdminTemplatePageController.class, "download");
        // api
        router.addMapper("/api" + Constants.ADMIN_URI_BASE_PATH, AdminController.class);
        router.addMapper("/api" + Constants.ADMIN_URI_BASE_PATH + "/link", LinkController.class);
        router.addMapper("/api" + Constants.ADMIN_URI_BASE_PATH + "/plugin", AdminController.class,"plugin");
        router.addMapper("/api" + Constants.ADMIN_URI_BASE_PATH + "/index", AdminController.class,"index");
        router.addMapper("/api" + Constants.ADMIN_URI_BASE_PATH + "/comment", CommentController.class);
        router.addMapper("/api" + Constants.ADMIN_URI_BASE_PATH + "/type", TypeController.class);
        router.addMapper("/api" + Constants.ADMIN_URI_BASE_PATH + "/article-type", TypeController.class,"index");
        router.addMapper("/api" + Constants.ADMIN_URI_BASE_PATH + "/article-edit", AdminArticleController.class,"articleEdit");
        router.addMapper("/api" + Constants.ADMIN_URI_BASE_PATH + "/nav", BlogNavController.class);
        router.addMapper("/api" + Constants.ADMIN_URI_BASE_PATH + "/article", AdminArticleController.class);
        router.addMapper("/api" + Constants.ADMIN_URI_BASE_PATH + "/website", WebSiteController.class);
        router.addMapper("/api" + Constants.ADMIN_URI_BASE_PATH + "/template", TemplateController.class);
        router.addMapper("/api" + Constants.ADMIN_URI_BASE_PATH + "/template-config", TemplateController.class,"configParams");
        router.addMapper("/api" + Constants.ADMIN_URI_BASE_PATH + "/upload", UploadController.class);
        router.addMapper("/api" + Constants.ADMIN_URI_BASE_PATH + "/upgrade", UpgradeController.class);
        router.addMapper("/api" + Constants.ADMIN_URI_BASE_PATH + "/user", AdminUserController.class);

    }

    /**
     * 添加浏览者能访问Control 路由
     *
     * @param router
     */
    public static void configBlogRouter(Router router) {
        router.addMapper("/api/public", BlogApiPublicController.class);
        router.addMapper(InstallUtils.INSTALL_ROUTER_PATH, InstallController.class);
        router.addMapper("/", ArticleController.class);
        router.addMapper("/api" + InstallUtils.INSTALL_ROUTER_PATH, ApiInstallController.class);
    }
}
