package com.fzb.blog.controlle;

import com.fzb.blog.dev.MailUtil;
import com.fzb.blog.model.Comment;
import com.fzb.blog.model.Log;
import com.fzb.blog.model.Type;
import com.fzb.blog.util.WebTools;
import com.fzb.common.util.ParseTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryLogController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryLogController.class);

    public void index() {
        if ((getRequest().getServletPath().startsWith("/post"))
                && (getPara(0) != null)) {
            if (getPara(0).equals("all")) {
                all();
            } else if (getPara(0) != null) {
                detail();
            }
        } else {
            all();
        }
    }

    public void search() {
        String key;
        if (getParaToInt(1) == null) {
            if (isNotNullOrNotEmptyStr(getPara("key"))) {
                key = convertRequestParam(getPara("key"));
                setAttr("data",
                        Log.dao.getLogsByTitleOrContent(1, getDefaultRows(), key));
            } else {
                all();
                removeSessionAttr("key");
                return;
            }

        } else {
            key = convertRequestParam(getPara(0));
            setAttr("data", Log.dao.getLogsByTitleOrContent(getParaToInt(1), getDefaultRows(), key));
        }
        // 记录回话的Key
        setSessionAttr("key", key);
        setAttr("yurl", "post/search/" + key + "-");

        setAttr("tipsType", "搜索");
        setAttr("tipsName", key);

        formatAlias(getAttr("data"));
    }

    private void formatAlias(Object data) {
        if (getStaticHtmlStatus()) {
            if (data instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) data;
                List<Log> logList = (List<Log>) map.get("rows");
                if (logList != null && logList.size() > 0) {
                    for (Log log : logList) {
                        log.set("alias", log.get("alias") + ".html");
                    }
                } else {
                    map.put("alias", map.get("alias") + ".html");
                }
            }
        }
    }

    public void record() {
        setAttr("data", Log.dao.getLogsByData(
                getParaToInt(1, 1),
                getDefaultRows(), getPara(0)));

        setAttr("yurl", "post/record/" + getPara(0) + "-");
        setAttr("tipsType", "存档");
        setAttr("tipsName", getPara(0));
        formatAlias(getAttr("data"));
    }

    public void addComment() {
        // TODO　如何过滤垃圾信息
        if (getPara("userComment") != null) {
            new Comment().set("userHome", getPara("userHome"))
                    .set("userMail", getPara("userMail"))
                    .set("userIp", WebTools.getRealIp(getRequest()))
                    .set("userName", getPara("userName"))
                    .set("logId", getPara("logId"))
                    .set("userComment", getPara("userComment"))
                    .set("commTime", new Date()).set("hide", 1).save();
        }
        detail(getPara("logId"));
        if (getStrValueByKey("commentEmailNotify") != null
                && "on".equals(getStrValueByKey("commentEmailNotify"))) {
            if (getStrValueByKey("mail_to") != null) {
                try {
                    MailUtil.sendMail(getStrValueByKey("mail_to"), getStrValueByKey("title") + "新的评论", "<h3>文章标题：" + Log.dao.findById(getPara("logId")).get("title")
                            + "</h3>\n" + getPara("userComment"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void detail() {
        detail(getPara());
    }

    private void detail(Object id) {
        Map<String, Object> data = Log.dao.getLogByLogId(id);
        if (data != null) {
            Integer logId = (Integer) data.get("logId");
            Map<String, Object> log = new HashMap<String, Object>();
            log.putAll(Log.dao.getLogByLogId(logId));
            Log.dao.clickChange(logId);
            log.put("lastLog", Log.dao.getLastLog(logId));
            log.put("nextLog", Log.dao.getNextLog(logId));
            log.put("comments", Comment.dao.getCommentsByLogId(logId));
            setAttr("log", log);
            formatAlias(log);
        }
    }

    public void sort() {
        setAttr("data", Log.dao.getLogsBySort(
                getParaToInt(1, 1),
                getDefaultRows(), getPara(0)));
        setAttr("yurl", "post/sort/" + getPara(0) + "-");
        Type t = Type.dao.findByAlias(getPara(0));

        setAttr("type", t);
        setAttr("tipsType", "分类");
        if (t != null) {
            setAttr("tipsName", t.getStr("typeName"));
        }
        formatAlias(getAttr("data"));
    }

    public void tag() {
        if (getPara(0) != null) {
            String tag = convertRequestParam(getPara(0));
            setAttr("data", Log.dao.getLogsByTag(
                    getParaToInt(1, 1),
                    getDefaultRows(), tag));

            setAttr("yurl", "post/tag/" + getPara(0) + "-");
            setAttr("tipsType", "标签");
            setAttr("tipsName", tag);
            formatAlias(getAttr("data"));
        }
    }

    public void all() {
        int page = ParseTools.strToInt(getPara(1), 1);
        setAttr("data", Log.dao.getLogsByPage(page, getDefaultRows()));
        setAttr("yurl", "post/all-");
        formatAlias(getAttr("data"));
    }

    private String convertRequestParam(String param) {
        if (param != null) {
            try {
                return URLDecoder.decode(param, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                LOGGER.error("request convert to UTF-8 error ", e);
            }
        }
        return "";
    }
}
