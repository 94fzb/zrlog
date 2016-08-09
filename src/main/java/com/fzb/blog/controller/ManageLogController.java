package com.fzb.blog.controller;

import com.fzb.blog.model.Log;
import com.fzb.blog.model.Tag;
import com.fzb.blog.model.User;
import com.fzb.blog.util.ResUtil;
import com.fzb.blog.util.ZrlogUtil;
import com.fzb.common.util.IOUtil;
import com.fzb.common.util.ParseTools;
import com.fzb.common.util.http.HttpUtil;
import com.fzb.common.util.http.handle.HttpJsonArrayHandle;
import com.jfinal.kit.PathKit;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

public class ManageLogController extends ManageController {

    private static final Logger LOGGER = Logger.getLogger(ManageLogController.class);

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
        Log log;
        if (isNotNullOrNotEmptyStr(getPara("logId"))) {
            log = new Log().getLogByLogIdA(getPara("logId"));
        } else {
            log = getLog();
        }
        Integer logId = log.getInt("logId");
        log.put("lastLog", Log.dao.getLastLog(logId, ResUtil.getStringFromRes("noLastLog", getRequest())));
        log.put("nextLog", Log.dao.getNextLog(logId, ResUtil.getStringFromRes("noNextLog", getRequest())));

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
        String uploadFieldName = "imgFile";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String fileExt = getFile().getFileName().substring(
                getFile(uploadFieldName).getFileName().lastIndexOf(".") + 1)
                .toLowerCase();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String uri = "/attached/" + getPara("dir") + "/"
                + sdf.format(new Date()) + "/" + df.format(new Date()) + "_"
                + new Random().nextInt(1000) + "." + fileExt;
        String finalFilePath = PathKit.getWebRootPath() + uri;

        IOUtil.moveOrCopyFile(PathKit.getWebRootPath() + "/attached/" + getFile(uploadFieldName).getFileName(), finalFilePath, true);
        getData().put("error", 0);
        getData().put("url", getCloudUrl(uri, finalFilePath));
        renderJson(getData());
    }

    private String getCloudUrl(String uri, String finalFilePath) {
        // try push to cloud
        Map<String, String[]> map = new HashMap<String, String[]>();
        map.put("fileInfo", new String[]{finalFilePath + "," + uri});
        map.put("name", new String[]{"uploadService"});
        String url;
        try {
            List<Map> urls = HttpUtil.sendGetRequest(ZrlogUtil.getPluginServer() + "/service", map
                    , new HttpJsonArrayHandle<Map>(), ZrlogUtil.genHeaderMapByRequest(getRequest())).getT();
            if (urls != null && !urls.isEmpty()) {
                url = (String) urls.get(0).get("url");
            } else {
                url = getRequest().getContextPath() + uri;
            }
        } catch (IOException e) {
            url = getRequest().getContextPath() + uri;
            LOGGER.error(e);
        }
        return url;
    }

    @Override
    public void queryAll() {
        renderJson(Log.dao.queryAll(getParaToInt("page"), getParaToInt("rows"), convertRequestParam(getPara("keywords")), getPara("sord"), getPara("sidx")));
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
