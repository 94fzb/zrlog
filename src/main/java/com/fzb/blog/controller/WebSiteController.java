package com.fzb.blog.controller;

import com.fzb.blog.model.WebSite;

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
        BaseController.refreshCache();
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
