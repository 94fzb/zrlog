package com.zrlog.blog.web.controller.api;

import com.zrlog.blog.web.controller.page.ArticleController;

/**
 * 对 ArticleController 的扩展，响应的数据均为Json格式
 */
public class ApiArticleController extends ArticleController {

    @Override
    public String detail() {
        return super.detail(getPara("id"));
    }

    @Override
    public void addComment() {
        saveComment();
    }
}
