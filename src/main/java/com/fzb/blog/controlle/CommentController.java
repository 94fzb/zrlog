package com.fzb.blog.controlle;

import com.fzb.blog.model.Comment;
import com.fzb.blog.util.DuoshuoUtil;
import com.fzb.blog.util.duoshuo.Meta;
import com.fzb.blog.util.duoshuo.ResponseEntry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jfinal.plugin.activerecord.Db;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentController extends ManageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommentController.class);

    public void delete() {
        String[] ids = getPara("id").split(",");
        for (String id : ids) {
            Comment.dao.deleteById(id);
        }
    }

    public void queryAll() {
        renderJson(Comment.dao.getCommentsByPage(getParaToInt("page"), getParaToInt("rows")));
    }

    @Override
    public void add() {

    }

    @Override
    public void update() {

    }

    public void refresh() {
        try {
            List<ResponseEntry> comments = DuoshuoUtil.getComments(getValueByKey("duoshuo_short_name").toString(), getValueByKey("duoshuo_secret").toString());
            if (!comments.isEmpty()) {
                // 清空表数据
                Db.update("TRUNCATE comment");
            }
            for (ResponseEntry entry : comments) {
                if (entry.getAction().equals("create")) {
                    Meta meta = new GsonBuilder().create().fromJson(new Gson().toJson(entry.getMeta()),
                            new TypeToken<Meta>() {
                            }.getType());
                    if (meta != null && meta.getThread_key() != null) {
                        DuoshuoUtil.convertToSelf(meta).save();
                    }
                }
                LOGGER.info("duoShuo action {} ", entry.getAction());
            }
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("status", 200);
            map.put("message", "更新完成");
            setAttr("data", map);
            renderJson(map);
        } catch (Exception e) {
            LOGGER.error("refresh sync error ", e);
        }
    }

}
