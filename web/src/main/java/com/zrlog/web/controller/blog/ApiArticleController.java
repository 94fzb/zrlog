package com.zrlog.web.controller.blog;

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
        renderJson(super.saveComment());
    }
}
