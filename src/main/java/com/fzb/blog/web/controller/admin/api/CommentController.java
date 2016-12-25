package com.fzb.blog.web.controller.admin.api;

import com.fzb.blog.model.Comment;
import com.fzb.blog.web.controller.BaseController;

import java.util.HashMap;
import java.util.Map;

public class CommentController extends BaseController {

    public Map delete() {
        String[] ids = getPara("id").split(",");
        for (String id : ids) {
            Comment.dao.deleteById(id);
        }
        return new HashMap<String, Object>();
    }

    public Map index() {
        return Comment.dao.getCommentsByPage(getParaToInt("page"), getParaToInt("rows"));
    }
}
