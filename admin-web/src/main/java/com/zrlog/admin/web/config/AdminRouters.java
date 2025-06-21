package com.zrlog.admin.web.config;

import com.hibegin.http.server.web.Router;
import com.zrlog.admin.web.controller.api.*;
import com.zrlog.admin.web.controller.page.AdminPageController;
import com.zrlog.admin.web.controller.page.AdminTemplatePageController;
import com.zrlog.business.service.TemplateInfoHelper;
import com.zrlog.common.AdminResource;
import com.zrlog.common.Constants;

public class AdminRouters {

    /**
     * 后台管理者路由，这里目前分为2中情况，及服务端响应模板页面和用户对数据的操作
     * 约定
     * 1. 添加页面需要响应时，使用 /admin 的路由，及同步操作
     * 2. 浏览数据，变更数据。使用 /api/admin 的路由，以JSON的格式进行响应，及异步操作
     */
    public static void configAdminRoute(Router router, AdminResource adminResource) {
        // 后台管理者
        router.addMapper(Constants.ADMIN_URI_BASE_PATH, AdminPageController.class);
        router.addMapper(Constants.ADMIN_URI_BASE_PATH + "/login", AdminPageController.class, "index");
        //error page
        adminResource.getAdminPageUris().forEach(uri -> router.addMapper(uri, AdminPageController.class, "index"));
        //template download
        router.addMapper(Constants.ADMIN_URI_BASE_PATH + "/template/download", AdminTemplatePageController.class, "download");
        router.addMapper(TemplateInfoHelper.ADMIN_PREVIEW_IMAGE_URI, AdminTemplatePageController.class, "previewImage");
        // api
        router.addMapper("/api" + Constants.ADMIN_URI_BASE_PATH, AdminController.class);
        router.addMapper("/api" + Constants.ADMIN_URI_BASE_PATH + "/403", AdminController.class, "error");
        router.addMapper("/api" + Constants.ADMIN_URI_BASE_PATH + "/404", AdminController.class, "error");
        router.addMapper("/api" + Constants.ADMIN_URI_BASE_PATH + "/500", AdminController.class, "error");
        router.addMapper("/api" + Constants.ADMIN_URI_BASE_PATH + "/offline", AdminController.class, "error");
        router.addMapper("/api" + Constants.ADMIN_URI_BASE_PATH + "/link", LinkController.class);
        router.addMapper("/api" + Constants.ADMIN_URI_BASE_PATH + "/plugin", AdminController.class, "plugin");
        router.addMapper("/api" + Constants.ADMIN_URI_BASE_PATH + "/index", AdminController.class, "index");
        router.addMapper("/api" + Constants.ADMIN_URI_BASE_PATH + "/comment", CommentController.class);
        router.addMapper("/api" + Constants.ADMIN_URI_BASE_PATH + "/type", TypeController.class);
        router.addMapper("/api" + Constants.ADMIN_URI_BASE_PATH + "/article-type", TypeController.class, "index");
        router.addMapper("/api" + Constants.ADMIN_URI_BASE_PATH + "/article-edit", AdminArticleController.class, "articleEdit");
        router.addMapper("/api" + Constants.ADMIN_URI_BASE_PATH + "/nav", BlogNavController.class);
        router.addMapper("/api" + Constants.ADMIN_URI_BASE_PATH + "/article", AdminArticleController.class);
        router.addMapper("/api" + Constants.ADMIN_URI_BASE_PATH + "/website", WebSiteController.class);
        router.addMapper("/api" + Constants.ADMIN_URI_BASE_PATH + "/website/template", TemplateController.class, "index");
        router.addMapper("/api" + Constants.ADMIN_URI_BASE_PATH + "/template", TemplateController.class);
        router.addMapper("/api" + Constants.ADMIN_URI_BASE_PATH + "/template-config", TemplateController.class, "configParams");
        router.addMapper("/api" + Constants.ADMIN_URI_BASE_PATH + "/template-center", TemplateController.class, "templateCenter");
        router.addMapper("/api" + Constants.ADMIN_URI_BASE_PATH + "/upload", UploadController.class);
        router.addMapper("/api" + Constants.ADMIN_URI_BASE_PATH + "/upgrade", UpgradeController.class);
        router.addMapper("/api" + Constants.ADMIN_URI_BASE_PATH + "/user", AdminUserController.class);
        router.addMapper("/api" + Constants.ADMIN_URI_BASE_PATH + "/system", AdminSystemController.class);

    }
}
