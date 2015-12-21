package com.fzb.blog.controlle;

import com.fzb.blog.model.Type;

public class TypeController extends ManageController {
	public void delete() {
		Type.dao.deleteById(getPara("id"));
	}

	public void queryAll() {
		renderJson(Type.dao
				.queryAll(getParaToInt("page"), getParaToInt("rows")));
	}

	@Override
	public void add() {
		new Type().set("typeName", getPara("typeName"))
				.set("alias", getPara("alias"))
				.set("remark", getPara("remark")).save();
	}

	@Override
	public void update() {
		new Type().set("typeId", getPara("id"))
				.set("typeName", getPara("typeName"))
				.set("alias", getPara("alias"))
				.set("remark", getPara("remark")).update();
	}
}
