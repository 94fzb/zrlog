package com.zrlog.web.config;

import com.jfinal.config.Routes;
import com.zrlog.web.controller.blog.api.ApiArticleController;
import com.zrlog.web.controller.blog.api.ApiInstallController;
import com.zrlog.web.controller.blog.api.BlogApiPublicController;
import com.zrlog.web.controller.blog.page.ArticleController;
import com.zrlog.web.controller.blog.page.InstallController;

import static com.zrlog.web.config.ZrLogConfig.INSTALL_ROUTER_PATH;

class BlogRoutes extends Routes {
    @Override
    public void config() {
        add("/", ArticleController.class);
        add(INSTALL_ROUTER_PATH, InstallController.class);
        add("/api" + INSTALL_ROUTER_PATH, ApiInstallController.class);
        // 添加浏览者能访问Control 路由
        add("/api/v1/" + com.zrlog.common.Constants.getArticleRoute(), ApiArticleController.class);
        add("/api/" + com.zrlog.common.Constants.getArticleRoute(), ApiArticleController.class);
        add("/api/public", BlogApiPublicController.class);
        if (!"".equals(com.zrlog.common.Constants.getArticleRoute())) {
            add("/" + com.zrlog.common.Constants.getArticleRoute(), ArticleController.class);
        }
    }
}
