package com.zrlog.web.controller.admin.api;

import com.zrlog.model.Comment;
import com.zrlog.web.controller.BaseController;

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
