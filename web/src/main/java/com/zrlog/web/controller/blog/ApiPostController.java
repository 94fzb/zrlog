package com.zrlog.web.controller.blog;

import java.io.IOException;

/**
 * 对 PostController 的扩展，响应的数据均为Json格式
 */
public class ApiPostController extends PostController {

    @Override
    public String detail() {
        return super.detail(getPara("id"));
    }

    public void addComment() throws IOException {
        renderJson(super.saveComment());
    }
}
