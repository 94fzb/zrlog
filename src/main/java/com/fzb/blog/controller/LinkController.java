package com.fzb.blog.controller;

import com.fzb.blog.model.Link;

public class LinkController extends ManageController {
    public void delete() {
        Link.dao.deleteById(getPara("id"));
    }

    public void update() {
        Link.dao.set("linkId", getPara("id"))
                .set("linkName", getPara("linkName"))
                .set("sort", getParaToInt("sort", 100))
                .set("url", getPara("url")).set("alt", getPara("alt")).update();
    }

    public void index() {
        render("/admin/link.jsp");
    }

    public void queryAll() {
        renderJson(Link.dao.queryAll(getParaToInt("page"), getParaToInt("rows")));
    }

    public void add() {
        new Link().set("linkName", getPara("linkName"))
                .set("sort", getParaToInt("sort", 100))
                .set("url", getPara("url")).set("alt", getPara("alt")).save();
    }

}
