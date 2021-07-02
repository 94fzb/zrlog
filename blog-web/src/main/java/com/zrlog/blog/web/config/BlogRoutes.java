package com.zrlog.blog.web.config;

import com.jfinal.config.Routes;
import com.zrlog.blog.web.controller.api.ApiArticleController;
import com.zrlog.blog.web.controller.api.ApiInstallController;
import com.zrlog.blog.web.controller.api.BlogApiPublicController;
import com.zrlog.blog.web.controller.page.ArticleController;
import com.zrlog.blog.web.controller.page.InstallController;
import com.zrlog.business.util.InstallUtils;

public class BlogRoutes extends Routes {
    @Override
    public void config() {
        add("/", ArticleController.class);
        add(InstallUtils.INSTALL_ROUTER_PATH, InstallController.class);
        add("/api" + InstallUtils.INSTALL_ROUTER_PATH, ApiInstallController.class);
        // 添加浏览者能访问Control 路由
        add("/api/v1/" + com.zrlog.common.Constants.getArticleRoute(), ApiArticleController.class);
        add("/api/" + com.zrlog.common.Constants.getArticleRoute(), ApiArticleController.class);
        add("/api/public", BlogApiPublicController.class);
        if (!"".equals(com.zrlog.common.Constants.getArticleRoute())) {
            add("/" + com.zrlog.common.Constants.getArticleRoute(), ArticleController.class);
        }
    }
}
