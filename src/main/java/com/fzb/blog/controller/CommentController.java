package com.fzb.blog.controller;

import com.fzb.blog.model.Comment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommentController extends ManageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommentController.class);

    public void delete() {
        String[] ids = getPara("id").split(",");
        for (String id : ids) {
            Comment.dao.deleteById(id);
        }
    }

    public void queryAll() {
        renderJson(Comment.dao.getCommentsByPage(getParaToInt("page"), getParaToInt("rows")));
    }

    @Override
    public void add() {

    }

    @Override
    public void update() {

    }
}
