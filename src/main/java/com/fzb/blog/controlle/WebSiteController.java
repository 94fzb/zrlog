package com.fzb.blog.controlle;

import com.fzb.blog.dev.MailUtil;
import com.fzb.blog.model.WebSite;

import java.util.Map;
import java.util.Map.Entry;

public class WebSiteController extends ManageController {
    public void update() {
        Map<String, String[]> tparamMap = getParaMap();
        for (Entry<String, String[]> param : tparamMap.entrySet()) {
            new WebSite().updateByKV(param.getKey(), param.getValue()[0],
                    !"-1".equals(param.getValue()[0]));
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

    public void testEmailService() {
        try {
            MailUtil.sendMail(getStrValueByKey("mail_to"), getStrValueByKey("title") + " 测试邮件", "<h3>这是一封测试邮件</h3>\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
        getData().put("send", true);
        renderJson(getData());
    }
}
