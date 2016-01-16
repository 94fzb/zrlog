package com.fzb.blog.controller;

import com.fzb.blog.model.LogNav;

public class LogNavController extends ManageController {
	public void delete() {
		LogNav.dao.deleteById(getPara(0));
	}

	public void queryAll() {
		renderJson(LogNav.dao.queryAll(getParaToInt("page"),
				getParaToInt("rows")));
	}

	@Override
	public void add() {
		new LogNav().set("navName", getPara("navName"))
				.set("url", getPara("url")).set("sort", getParaToInt("sort"))
				.save();
	}

	@Override
	public void update() {
		new LogNav().set("navId", getPara("id"))
				.set("navName", getPara("navName")).set("url", getPara("url"))
				.set("sort", getParaToInt("sort")).update();
	}

}
