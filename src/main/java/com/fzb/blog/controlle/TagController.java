package com.fzb.blog.controlle;

import com.fzb.blog.model.Link;
import com.fzb.blog.model.Tag;

public class TagController extends ManageController {
	public void delete() {
		Link.dao.deleteById(getPara(0));
	}

	public void queryAll() {
		Tag.dao.refreshTag();
		renderJson(Tag.dao.queryAll(getParaToInt("page"), getParaToInt("rows")));
	}

	@Override
	public void add() {
		
	}

	@Override
	public void update() {
		
	}

}
