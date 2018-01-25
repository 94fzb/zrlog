package com.zrlog.web.controller.blog;

import com.zrlog.model.Log;
import com.zrlog.service.CacheService;
import com.zrlog.model.Comment;
import com.zrlog.model.Type;
import com.zrlog.service.ArticleService;
import com.zrlog.util.I18NUtil;
import com.zrlog.util.ParseUtil;
import com.zrlog.web.controller.BaseController;
import com.zrlog.web.util.WebTools;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

public class PostController extends BaseController {

    private ArticleService articleService = new ArticleService();

    /**
     * add page info for template more easy
     *
     * @param currentUri
     * @param data
     * @param currentPage
     */
    private void setPageInfo(String currentUri, Map<String, Object> data, int currentPage) {
        setAttr("yurl", currentUri);

        Integer total = (Integer) data.get("total");
        if (total != null) {
            setAttr("data", data);
            if (total > 1) {
                fullPager(currentUri, currentPage, total);
            }
        }
    }

    private void fullPager(String currentUri, int currentPage, Integer total) {
        Map<String, Object> pager = new HashMap<>();
        List<Map<String, Object>> pageList = new ArrayList<>();
        if (currentPage != 1) {
            pageList.add(pageEntity(currentUri, currentPage, I18NUtil.getStringFromRes("prevPage", getRequest()), currentPage - 1));
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
            pageList.add(pageEntity(currentUri, currentPage, I18NUtil.getStringFromRes("nextPage", getRequest()), currentPage + 1));
        }
        pager.put("pageList", pageList);
        pager.put("pageStartUrl", currentUri + 1);
        pager.put("pageEndUrl", currentUri + total);
        pager.put("startPage", currentPage == 1);
        pager.put("endPage", currentPage == total);
        setAttr("pager", pager);
    }

    private Map<String, Object> pageEntity(String url, int currentPage, String desc, int page) {
        Map<String, Object> map = new HashMap<>();
        map.put("url", url + page);
        map.put("desc", desc);
        map.put("current", currentPage == page);
        return map;
    }

    private Map<String, Object> pageEntity(String url, int currentPage, int page) {
        return pageEntity(url, currentPage, page + "", page);
    }


    public String index() {
        if ((getRequest().getServletPath().startsWith("/post"))
                && (getPara(0) != null)) {
            if (getPara(0).equals("all")) {
                return all();
            } else if (getPara(0) != null) {
                return detail();
            } else {
                return all();
            }
        } else {
            return all();
        }
    }

    public String search() {
        String key;
        Map<String, Object> data;
        if (getParaToInt(1) == null) {
            if (isNotNullOrNotEmptyStr(getPara("key"))) {
                if ("GET".equals(getRequest().getMethod())) {
                    key = convertRequestParam(getPara("key"));
                } else {
                    key = getPara("key");
                }
                data = articleService.searchArticle(1, getDefaultRows(), key);
            } else {
                return all();
            }

        } else {
            key = convertRequestParam(getPara(0));
            data = articleService.searchArticle(getParaToInt(1), getDefaultRows(), key);
        }
        // 记录回话的Key
        setAttr("key", WebTools.htmlEncode(key));

        setAttr("tipsType", I18NUtil.getStringFromRes("search", getRequest()));
        setAttr("tipsName", WebTools.htmlEncode(key));

        setPageInfo("post/search/" + key + "-", data, getParaToInt(1, 1));
        return "page";
    }

    public String record() {
        setAttr("tipsType", I18NUtil.getStringFromRes("archive", getRequest()));
        setAttr("tipsName", getPara(0));

        setPageInfo("post/record/" + getPara(0) + "-", Log.dao.getLogsByData(getParaToInt(1, 1), getDefaultRows(), getPara(0)), getParaToInt(1, 1));
        return "page";
    }

    public void addComment() throws UnsupportedEncodingException {
        Integer logId = getParaToInt("logId");
        if (logId != null && getPara("userComment") != null) {
            Log log = Log.dao.getLogById(logId);
            if (log != null && log.getBoolean("canComment")) {
                String comment = Jsoup.clean(getPara("userComment"), Whitelist.basic());
                if (comment.length() > 0) {
                    // TODO　如何过滤垃圾信息
                    new Comment().set("userHome", getPara("userHome"))
                            .set("userMail", getPara("userMail"))
                            .set("userIp", WebTools.getRealIp(getRequest()))
                            .set("userName", getPara("userName"))
                            .set("logId", logId)
                            .set("userComment", comment)
                            .set("commTime", new Date()).set("hide", 1).save();
                }
                String alias = URLEncoder.encode(log.getStr("alias"), "UTF-8");
                String ext = "";
                if (isStaticHtmlStatus()) {
                    ext = ".html";
                    new CacheService().clearStaticPostFileByLogId(logId + "");
                }
                if (getRequest().getContextPath().isEmpty()) {
                    redirect("/post/" + alias + ext);
                } else {
                    redirect(getRequest().getContextPath() + "post/" + alias + ext);
                }
            } else {
                renderError(404);
            }
        }
    }

    public String detail() {
        return detail(convertRequestParam(getPara()));
    }

    private String detail(Object id) {
        Log log = Log.dao.getLogById(id);
        if (log != null) {
            Integer logId = log.get("logId");
            Log.dao.clickChange(logId);
            log.put("lastLog", Log.dao.getLastLog(logId, I18NUtil.getStringFromRes("noLastLog", getRequest())));
            log.put("nextLog", Log.dao.getNextLog(logId, I18NUtil.getStringFromRes("noNextLog", getRequest())));
            log.put("comments", Comment.dao.getCommentsByLogId(logId));
            setAttr("log", log);
        }
        return "detail";
    }

    public String sort() {
        String typeStr = convertRequestParam(getPara(0));
        setPageInfo("post/sort/" + typeStr + "-", Log.dao.getLogsBySort(getParaToInt(1, 1), getDefaultRows(), typeStr), getParaToInt(1, 1));

        Type type = Type.dao.findByAlias(typeStr);
        setAttr("type", type);
        setAttr("tipsType", I18NUtil.getStringFromRes("category", getRequest()));
        if (type != null) {
            setAttr("tipsName", type.getStr("typeName"));
        }
        return "page";
    }

    public String tag() {
        if (getPara(0) != null) {
            String tag = convertRequestParam(getPara(0));
            setPageInfo("post/tag/" + getPara(0) + "-", Log.dao.getLogsByTag(getParaToInt(1, 1), getDefaultRows(), tag), getParaToInt(1, 1));

            setAttr("tipsType", I18NUtil.getStringFromRes("tag", getRequest()));
            setAttr("tipsName", tag);
        }
        return "page";
    }

    public String tags() {
        return "tags";
    }

    public String all() {
        int page = ParseUtil.strToInt(getPara(1), 1);
        Map<String, Object> data = Log.dao.getLogsByPage(page, getDefaultRows());
        setPageInfo("post/all-", data, page);
        return "index";
    }
}
