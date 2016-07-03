package com.fzb.blog.controller;

import com.jfinal.plugin.ehcache.CacheKit;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public abstract class ManageController extends BaseController {

    private static Logger LOGGER = Logger.getLogger(ManageLogController.class);

    private Map<String, Object> data = new HashMap<String, Object>();

    protected void renderNotPage() {
        render("/admin/error/404.jsp");
    }

    protected void renderInternalServerErrorPage() {
        render("/admin/error/500.jsp");
    }

    protected Map<String, Object> getData() {
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
                LOGGER.warn("unSupport ");
            }
            Map map = new HashMap<String, Object>();
            map.put(getPara("oper"), true);
            renderJson(map);
        }

        // 清空数据缓存
       cleanCache();
    }


    public void cleanCache() {
        CacheKit.remove("/post/initData", "initData");
    }

    protected void put(String key, Object value) {
        data.put(key, value);
    }

    public abstract void add();

    public abstract void update();

    public abstract void delete();

    public abstract void queryAll();

}
