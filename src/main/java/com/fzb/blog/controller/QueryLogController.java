package com.fzb.blog.controller;

import com.fzb.blog.model.Comment;
import com.fzb.blog.model.Log;
import com.fzb.blog.model.Type;
import com.fzb.blog.util.ResUtil;
import com.fzb.blog.util.WebTools;
import com.fzb.common.util.ParseTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class QueryLogController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryLogController.class);


    private static final int DIGEST_LENGTH = 200;

    /**
     * add page info for template more easy
     *
     * @param currentUri
     * @param data
     * @param currentPage
     */
    private void setPageInfo(String currentUri, Map<String, Object> data, int currentPage) {
        setAttr("yurl", currentUri);

        currentUri = getRequest().getScheme() + "://" + getRequest().getHeader("host") + getRequest().getContextPath() + "/" + currentUri;
        Integer total = (Integer) data.get("total");
        if (total != null) {
            setAttr("data", data);
            if (total > 1) {
                fullPager(currentUri, currentPage, total);
            }
        }
    }

    private void wrapperSearchKeyword(List<Log> logs, String keyword) {
        for (Log log : logs) {
            String title = log.get("title").toString();
            String content = log.get("content").toString();
            String digest = log.get("digest").toString();
            log.put("title", wrapper(title, keyword));
            String tryWrapperDigest = wrapper(digest, keyword);
            boolean findInDigest = tryWrapperDigest.length() != digest.length();

            if (findInDigest) {
                log.put("digest", tryWrapperDigest);
            } else {
                String wrapperContent = wrapper(ParseTools.removeHtmlElement(content), keyword);
                log.put("digest", wrapperContent);
            }
        }
    }

    private String wrapper(String content, String keyword) {
        String newContent = content;
        if (content.contains(keyword)) {
            newContent = content.replace(keyword, wrapperFontRed(keyword));
        } else {
            String lowerContent = content.toLowerCase();
            if (lowerContent.contains(keyword.toLowerCase())) {
                String[] strings = lowerContent.split(keyword.toLowerCase());
                StringBuilder sb = new StringBuilder();
                int count = 0;
                if (strings.length > 1) {
                    for (int i = 0; i < strings.length - 1; i++) {
                        count += strings[i].length();
                        String str = wrapperFontRed(content.substring(count, count + keyword.length()));
                        System.out.println(str);
                        System.out.println(sb.toString());
                        sb.append(content.substring(count - strings[i].length(), count));
                        sb.append(str);
                        count += keyword.length();
                    }
                    // 添加最后一段数据
                    sb.append(content.substring(count).replace(keyword.toLowerCase(), wrapperFontRed(keyword)));
                    newContent = sb.toString();
                } else if (strings.length > 0) {
                    sb.append(content.substring(0, strings[0].length()));
                    sb.append(lowerContent.substring(strings[0].length()).replace(keyword.toLowerCase(), wrapperFontRed(content.substring(strings[0].length()))));
                    newContent = sb.toString();
                } else {
                    newContent = wrapperFontRed(content);
                }
            }
        }
        if (newContent.length() != content.length()) {
            int first = newContent.indexOf("<font") - 5;
            if (first > -1 && newContent.length() > DIGEST_LENGTH) {
                String[] fontArr = (newContent.substring(first)).split("</font>");
                newContent = "";
                for (String str : fontArr) {
                    if (!"".equals(newContent) && newContent.length() + str.length() > DIGEST_LENGTH) {
                        if (newContent.length() < 100) {
                            newContent += str.substring(0, 100);
                        }
                        break;
                    }
                    newContent += str + "</font>";
                }
            }
        }
        return newContent;
    }

    private String wrapperFontRed(String content) {
        return "<font color=\"#CC0000\">" + content + "</font>";
    }

    private void fullPager(String currentUri, int currentPage, Integer total) {
        Map<String, Object> pager = new HashMap<String, Object>();
        List<Map<String, Object>> pageList = new ArrayList<Map<String, Object>>();
        if (currentPage != 1) {
            pageList.add(pageEntity(currentUri, currentPage, ResUtil.getStringFromRes("prevPage", getRequest()), currentPage - 1));
        }
        if (total > 10) {
            if (currentPage < 3 || total - 4 < currentPage) {
                for (int i = 1; i <= 4; i++) {
                    pageList.add(pageEntity(currentUri, currentPage, i));
                }
            } else {
                if (currentPage + 1 == total - 3) {
                    pageList.add(pageEntity(currentUri, currentPage, currentPage - 3));
                }
                for (int i = currentPage - 2; i <= currentPage; i++) {
                    pageList.add(pageEntity(currentUri, currentPage, i));
                }
                if (currentPage + 1 != total - 3) {
                    pageList.add(pageEntity(currentUri, currentPage, currentPage + 1));
                }
            }
            for (int i = total - 3; i <= total; i++) {
                pageList.add(pageEntity(currentUri, currentPage, i));
            }
        } else {
            for (int i = 1; i <= total; i++) {
                pageList.add(pageEntity(currentUri, currentPage, i));
            }
        }
        if (currentPage != total) {
            pageList.add(pageEntity(currentUri, currentPage, ResUtil.getStringFromRes("nextPage", getRequest()), currentPage + 1));
        }
        pager.put("pageList", pageList);
        pager.put("startPage", currentPage == 1);
        pager.put("endPage", currentPage == total);
        setAttr("pager", pager);
    }

    private Map<String, Object> pageEntity(String url, int currentPage, String desc, int page) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("url", url + page);
        map.put("desc", desc);
        map.put("current", currentPage == page);
        return map;
    }

    private Map<String, Object> pageEntity(String url, int currentPage, int page) {
        return pageEntity(url, currentPage, page + "", page);
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
        Map<String, Object> data;
        if (getParaToInt(1) == null) {
            if (isNotNullOrNotEmptyStr(getPara("key"))) {
                if ("GET".equals(getRequest().getMethod())) {
                    key = convertRequestParam(getPara("key"));
                } else {
                    key = getPara("key");
                }
                data = Log.dao.getLogsByTitleOrContent(1, getDefaultRows(), key);
            } else {
                all();
                removeSessionAttr("key");
                return;
            }

        } else {
            key = convertRequestParam(getPara(0));
            data = Log.dao.getLogsByTitleOrContent(getParaToInt(1), getDefaultRows(), key);
        }
        // 记录回话的Key
        setSessionAttr("key", key);

        setAttr("tipsType", ResUtil.getStringFromRes("search", getRequest()));
        setAttr("tipsName", key);

        setPageInfo("post/search/" + key + "-", data, getParaToInt(1, 1));
        formatAlias(getAttr("data"));
        List<Log> logs = (List<Log>) data.get("rows");
        if (logs != null && !logs.isEmpty()) {
            wrapperSearchKeyword(logs, key);
        }
    }

    public void record() {
        setAttr("tipsType", ResUtil.getStringFromRes("archive", getRequest()));
        setAttr("tipsName", getPara(0));

        setPageInfo("post/record/" + getPara(0) + "-", Log.dao.getLogsByData(getParaToInt(1, 1), getDefaultRows(), getPara(0)), getParaToInt(1, 1));
        formatAlias(getAttr("data"));
    }

    public void addComment() {
        if (getPara("logId") != null && getPara("userComment") != null) {
            // TODO　如何过滤垃圾信息
            new Comment().set("userHome", getPara("userHome"))
                    .set("userMail", getPara("userMail"))
                    .set("userIp", WebTools.getRealIp(getRequest()))
                    .set("userName", getPara("userName"))
                    .set("logId", getPara("logId"))
                    .set("userComment", getPara("userComment"))
                    .set("commTime", new Date()).set("hide", 1).save();
            detail(getPara("logId"));
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
            log.put("lastLog", Log.dao.getLastLog(logId, ResUtil.getStringFromRes("noLastLog", getRequest())));
            log.put("nextLog", Log.dao.getNextLog(logId, ResUtil.getStringFromRes("noNextLog", getRequest())));
            log.put("comments", Comment.dao.getCommentsByLogId(logId));
            setAttr("log", log);
            formatAlias(log);
        }
    }

    public void sort() {
        setPageInfo("post/sort/" + getPara(0) + "-", Log.dao.getLogsBySort(getParaToInt(1, 1), getDefaultRows(), getPara(0)), getParaToInt(1, 1));

        Type type = Type.dao.findByAlias(getPara(0));
        setAttr("type", type);
        setAttr("tipsType", ResUtil.getStringFromRes("category", getRequest()));
        if (type != null) {
            setAttr("tipsName", type.getStr("typeName"));
        }
        formatAlias(getAttr("data"));
    }

    public void tag() {
        if (getPara(0) != null) {
            String tag = convertRequestParam(getPara(0));
            setPageInfo("post/tag/" + getPara(0) + "-", Log.dao.getLogsByTag(getParaToInt(1, 1), getDefaultRows(), tag), getParaToInt(1, 1));

            setAttr("tipsType", ResUtil.getStringFromRes("tag", getRequest()));
            setAttr("tipsName", tag);
            formatAlias(getAttr("data"));
        }
    }

    public void all() {
        int page = ParseTools.strToInt(getPara(1), 1);
        Map<String, Object> data = Log.dao.getLogsByPage(page, getDefaultRows());
        setPageInfo("post/all-", data, page);
        formatAlias(data);
    }
}
