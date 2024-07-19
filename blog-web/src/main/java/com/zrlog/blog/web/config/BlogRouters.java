package com.zrlog.blog.web.config;

import com.hibegin.http.server.web.Router;
import com.zrlog.blog.web.controller.api.ApiInstallController;
import com.zrlog.blog.web.controller.api.BlogApiArticleController;
import com.zrlog.blog.web.controller.api.BlogApiCacheController;
import com.zrlog.blog.web.controller.api.BlogApiPublicController;
import com.zrlog.blog.web.controller.page.ArticleController;
import com.zrlog.blog.web.controller.page.InstallController;
import com.zrlog.business.util.InstallUtils;

public class BlogRouters {

    /**
     * 添加浏览者能访问Control 路由
     *
     * @param router
     */
    public static void configBlogRouter(Router router) {
        router.addMapper("/api/public", BlogApiPublicController.class);
        router.addMapper(InstallUtils.INSTALL_ROUTER_PATH, InstallController.class);
        router.addMapper("/", ArticleController.class);
        router.addMapper("/api/article", BlogApiArticleController.class);
        router.addMapper("/api/cache", BlogApiCacheController.class);
        router.addMapper("/api" + InstallUtils.INSTALL_ROUTER_PATH, ApiInstallController.class);
    }
}
