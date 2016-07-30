package com.fzb.blog.controller;

import com.fzb.blog.model.WebSite;
import com.fzb.blog.plugin.UpdateVersionPlugin;
import com.jfinal.config.Plugins;
import com.jfinal.core.JFinal;
import com.jfinal.plugin.IPlugin;

import java.util.Map;
import java.util.Map.Entry;

public class WebSiteController extends ManageController {
    public void update() {
        Map<String, String[]> tmpParamMap = getParaMap();
        for (Entry<String, String[]> param : tmpParamMap.entrySet()) {
            new WebSite().updateByKV(param.getKey(), param.getValue()[0]);
        }
        if (getPara("resultType") != null
                && "html".equals(getPara("resultType"))) {
            setAttr("message", "变更完成");
        } else {
            getData().put("success", true);
            renderJson(getData());
        }
        // 更新缓存数据
        cleanCache();
    }

    public void upgrade() {
        update();
        Plugins plugins = (Plugins) JFinal.me().getServletContext().getAttribute("plugins");
        if (getParaToInt("autoUpgradeVersion") == -1) {
            for (IPlugin plugin : plugins.getPluginList()) {
                if (plugin instanceof UpdateVersionPlugin) {
                    plugin.stop();
                }
            }
        } else {
            for (IPlugin plugin : plugins.getPluginList()) {
                if (plugin instanceof UpdateVersionPlugin) {
                    plugin.start();
                }
            }
        }
    }

    @Override
    public void add() {

    }

    @Override
    public void queryAll() {

    }

    @Override
    public void delete() {

    }
}
