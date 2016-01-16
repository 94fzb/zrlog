package com.fzb.blog.controller;

import com.fzb.blog.dev.MailUtil;
import com.fzb.blog.model.Log;
import com.fzb.blog.util.DuoshuoUtil;
import com.fzb.blog.util.duoshuo.Meta;
import com.fzb.blog.util.duoshuo.ResponseEntry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jfinal.plugin.activerecord.Db;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhengchangchun 对QueryLogControl 的扩展 响应的数据均为Json格式
 */
public class APIController extends QueryLogController {

    private static final Logger LOGGER = LoggerFactory.getLogger(APIController.class);

    /**
     * 多说反向同步接口
     */
    public void duoshuo() {
        Map<String, Object> param = getdouShuoRequest();
        String action = (String) param.get("action");
        String signature = (String) param.get("signature");
        param.remove("signature");
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            // check signature
            if (!signature.equals(DuoshuoUtil.hmacSHA1Encrypt(param, getStrValueByKey("duoshuo_secret")))) {
                map.put("status", 400);
            } else {
                // 使用签名
                ResponseEntry entry = DuoshuoUtil.getCommentLast(getStrValueByKey("duoshuo_short_name"), getStrValueByKey("duoshuo_secret"));
                if (entry != null) {
                    if (entry.getAction().equals("create")) {
                        Meta meta = new GsonBuilder().create().fromJson(new Gson().toJson(entry.getMeta()),
                                new TypeToken<Meta>() {
                                }.getType());

                        DuoshuoUtil.convertToSelf(meta).save();
                        if (getStrValueByKey("commentEmailNotify") != null
                                && "on".equals(getStrValueByKey("commentEmailNotify"))) {
                            if (getStrValueByKey("mail_to") != null) {
                                MailUtil.sendMail(getStrValueByKey("mail_to"), getStrValueByKey("title") + "新的评论", "<h3>文章标题：" + Log.dao.findById(meta.getThread_key()).get("title")
                                        + "</h3>\n" + meta.getMessage());
                            }
                        }
                    } else if (entry.getAction().equals("delete")) {
                        List<String> l = (List<String>) entry.getMeta();
                        for (String str : l) {
                            Db.update("delete from comment where postID=?", str);
                        }
                    }
                }
            }
            map.put("status", 200);
        } catch (Exception e) {
            map.put("status", 500);
            LOGGER.error("doushuo sync error ", e);
        }
        LOGGER.info("action {}", action);
        setAttr("data", map);
    }

    private Map<String, Object> getdouShuoRequest() {
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setHeaderEncoding("UTF-8");

        Map<String, Object> map = new HashMap<String, Object>();
        try {
            List items = upload.parseRequest(getRequest());
            for (Object item1 : items) {
                FileItem item = (FileItem) item1;
                if (item.isFormField()) {
                    map.put(item.getFieldName(), item.getString());
                }
            }
        } catch (FileUploadException e) {
            LOGGER.error("parse duoshou param error ", e);
        }
        return map;
    }
}
