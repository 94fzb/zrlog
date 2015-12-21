package com.fzb.blog.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebSite extends Model<WebSite> {
	public static final WebSite dao = new WebSite();

	public Map<String, Object> getWebSite() {
		Map<String, Object> webSites = new HashMap<String, Object>();
		List<WebSite> lw = find("select * from website");
		for (WebSite webSite : lw) {
			webSites.put(webSite.getStr("name"), webSite.get("value"));
			webSites.put(webSite.getStr("name") + "Status",
					webSite.getBoolean("status"));
			webSites.put(webSite.getStr("name") + "Remark",
					webSite.get("remark"));
		}
		return webSites;
	}

	public boolean updateByKV(String name, String value, boolean status) {
		if (Db.queryInt("select siteid from website where name=?", name) != null) {
			Db.update("update website set value=?,status=? where name=?",
					value, status, name);
		} else {
			Db.update("insert website(`value`,`name`,`status`) value(?,?,?)",
					value, name, status);
		}
		return true;

	}
}
