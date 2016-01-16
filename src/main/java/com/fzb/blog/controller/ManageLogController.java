package com.fzb.blog.controller;

import com.fzb.blog.model.Log;
import com.fzb.blog.model.Tag;
import com.fzb.blog.model.User;
import com.fzb.common.util.IOUtil;
import com.fzb.common.util.ParseTools;
import com.fzb.io.api.FileManageAPI;
import com.fzb.io.yunstore.BucketVO;
import com.fzb.io.yunstore.QiniuBucketManageImpl;
import com.jfinal.kit.PathKit;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class ManageLogController extends ManageController {

    public void timeline() {
        render("/admin/ext/timeline.jsp");
    }

    @Override
    public void add() {
        Log log = getLog();
        if (log.get("rubbish")) {
            getSession().setAttribute("log", log);
        } else {
            getSession().removeAttribute("log");
            Tag.dao.insertTag(getPara("keywords"));
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("add", log.save());
        map.put("logId", log.get("logId"));
        map.put("alias", log.get("alias"));
        renderJson(map);
    }

    public void update() {
        // compare tag
        String oldTagStr = Log.dao.findById(Integer.parseInt(getPara("logId"))).get("keywords");
        Tag.dao.update(getPara("keywords"), oldTagStr);
        Log log = getLog();
        log.update();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("update", true);
        map.put("logId", log.get("logId"));
        map.put("alias", log.get("alias"));
        renderJson(map);
        getSession().removeAttribute("log");
    }

    public void preview() {
        Integer logId;
        Log log;
        if (getPara("logId") != null) {
            log = new Log().getLogByLogIdA(getPara("logId"));
        } else {
            log = getLog();
        }
        logId = log.getInt("logId");
        log.put("lastLog", Log.dao.getLastLog(logId));
        log.put("nextLog", Log.dao.getNextLog(logId));
        setAttr("log", log.getAttrs());
        render(getTemplatePath() + "/detail.jsp");
    }

    public void editFrame() {
        Integer logId = Integer.parseInt(getPara("logId"));
        setAttr("log", Log.dao.getLogByLogIdA(logId).getAttrs());
        render("/admin/edit_frame.jsp");
    }

    public void delete() {
        String ids[] = getPara("id").split(",");
        for (String id : ids) {
            delete(id);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("delete", true);
        renderJson(map);
    }

    private void delete(Object logId) {
        if (logId != null) {
            Log log = Log.dao.getLogByLogIdA(logId);
            if (log != null && log.get("keywords") != null) {
                Tag.dao.deleteTag(log.get("keywords").toString());
            }
            Log.dao.deleteById(logId);
        }
    }

    public void createOrUpdate() {
        Object logId = getPara("logId");
        if (!isNotNullOrNotEmptyStr(logId)) {
            add();
        } else {
            update();
        }
        // 移除缓存文件
        if (getStaticHtmlStatus()) {
            Log log = Log.dao.findById(logId);
            if (getPara("logId") != null) {
                File file = new File(PathKit.getWebRootPath() + "/post/" + getPara("logId") + ".html");
                file.delete();
            }
            if (log != null) {
                File aliasFile = new File(PathKit.getWebRootPath() + "/post/" + log.get("alias") + ".html");
                aliasFile.delete();
            }
        }
    }

    public void upload() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String fileExt = getFile("imgFile")
                .getFileName()
                .substring(
                        getFile("imgFile").getFileName().lastIndexOf(".") + 1)
                .toLowerCase();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String url = "/attached/" + getPara("dir") + "/"
                + sdf.format(new Date()) + "/" + df.format(new Date()) + "_"
                + new Random().nextInt(1000) + "." + fileExt;
        IOUtil.moveOrCopyFile(PathKit.getWebRootPath() + "/attached/"
                + getFile("imgFile").getFileName(), PathKit.getWebRootPath()
                + url, true);
        getData().put("error", 0);

        // put to cloud
        String prefix = getStrValueByKey("bucket_type");
        if (prefix != null) {
            BucketVO bucket = new BucketVO(
                    getStrValueByKey(prefix + "_bucket"),
                    getStrValueByKey(prefix + "_access_key"),
                    getStrValueByKey(prefix + "_secret_key"),
                    getStrValueByKey(prefix + "_host"));
            FileManageAPI man = new QiniuBucketManageImpl(bucket);
            String newUrl = man
                    .create(new File(PathKit.getWebRootPath() + url), url)
                    .get("url").toString();
            getData().put("url", newUrl);
        } else {
            if (getRequest().getContextPath() != null) {
                url = getRequest().getContextPath() + url;
            }
            getData().put("url", url);
        }
        renderJson(getData());
    }

    @Override
    public void queryAll() {
        renderJson(Log.dao.queryAll(getParaToInt("page"), getParaToInt("rows"), convertRequestParam(getPara("keywords"))));
    }

    private Log getLog() {
        Map<String, String[]> param = getRequest().getParameterMap();
        Log log = new Log();
        for (Entry<String, String[]> map : param.entrySet()) {
            if (map.getValue().length > 0) {
                log.set(map.getKey(), map.getValue()[0]);
            }
        }
        Object logId = log.get("logId");
        if (!isNotNullOrNotEmptyStr(logId)) {
            logId = log.getMaxRecord() + 1;
            log.set("logId", logId);
            log.set("releaseTime", new Date());
            if (log.get("alias") == null || "".equals((log.get("alias") + "").trim())) {
                log.set("alias", logId);
            }
        } else {
            log.set("alias", (log.get("alias") + "").trim().replace(" ", "-"));
        }
        log.set("userId", ((User) getSessionAttr("user")).getInt("userId"));

        log.set("canComment", param.get("canComment") != null);
        log.set("recommended", param.get("recommended") != null);
        log.set("private", param.get("private") != null);
        log.set("rubbish", param.get("rubbish") != null);
        // 自动摘要
        if (log.get("digest") == null || "".equals(log.get("digest"))) {
            log.set("digest", ParseTools.autoDigest(log.get("content").toString(), 200));
        }
        return log;
    }
}
