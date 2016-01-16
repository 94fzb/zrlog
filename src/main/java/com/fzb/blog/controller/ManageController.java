package com.fzb.blog.controller;

import java.util.HashMap;
import java.util.Map;

public abstract class ManageController extends BaseController {
	private Map<String, Object> data = new HashMap<String, Object>();

	public Map<String, Object> getData() {
		return this.data;
	}

	public void oper() {
		if (getPara("oper") != null) {
			if ("del".equals(getPara("oper"))) {
				this.delete();
			} else if ("update".equals(getPara("oper"))
					|| "edit".equals(getPara("oper"))) {
				this.update();
			} else if ("add".equals(getPara("oper"))) {
				this.add();
			} else {
				System.out.println("unSupport ");
			}
			Object map = new HashMap<String,Object>();
			((Map) map).put(getPara("oper"),true);
			renderJson(map);

		}
		
		// 清空数据缓存
		BaseController.refreshCache();
	}
	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	public void put(String key, Object value) {
		data.put(key, value);
	}

	public abstract void add();

	public abstract void update();

	public abstract void delete();

	public abstract void queryAll();

}
