package com.zrlog.web.controller.blog;

import com.zrlog.common.Constants;
import com.zrlog.common.request.CreateCommentRequest;
import com.zrlog.common.response.CreateCommentResponse;
import com.zrlog.model.Comment;
import com.zrlog.model.Log;
import com.zrlog.model.Type;
import com.zrlog.service.ArticleService;
import com.zrlog.web.cache.CacheService;
import com.zrlog.service.CommentService;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.PagerUtil;
import com.zrlog.util.ParseUtil;
import com.zrlog.util.ZrLogUtil;
import com.zrlog.web.controller.BaseController;
import com.zrlog.web.handler.GlobalResourceHandler;
import com.zrlog.web.util.WebTools;

import java.util.Map;

public class ArticleController extends BaseController {

    private ArticleService articleService = new ArticleService();

    private CommentService commentService = new CommentService();

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
                setAttr("pager", PagerUtil.generatorPager(currentUri, currentPage, total));
            }
        }
    }

    public String index() {
        if (getPara(0) != null) {
            if ("all".equals(getPara(0))) {
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

        setAttr("tipsType", I18nUtil.getStringFromRes("search"));
        setAttr("tipsName", WebTools.htmlEncode(key));

        setPageInfo(Constants.getArticleUri() + "search/" + key + "-", data, getParaToInt(1, 1));
        return "page";
    }

    public String record() {
        setAttr("tipsType", I18nUtil.getStringFromRes("archive"));
        setAttr("tipsName", getPara(0));

        setPageInfo(Constants.getArticleUri() + "record/" + getPara(0) + "-", new Log().findByDate(getParaToInt(1, 1), getDefaultRows(), getPara(0)), getParaToInt(1, 1));
        return "page";
    }

    public void addComment() {
        CreateCommentResponse response = saveComment();
        String ext = "";
        if (Constants.isStaticHtmlStatus()) {
            ext = ".html";
            new CacheService().refreshInitDataCache(GlobalResourceHandler.CACHE_HTML_PATH, this, true);
        }
        redirect("/" + Constants.getArticleUri() + response.getAlias() + ext);
    }

    CreateCommentResponse saveComment() {
        CreateCommentRequest createCommentRequest = ZrLogUtil.convertRequestParam(getRequest().getParameterMap(), CreateCommentRequest.class);
        createCommentRequest.setIp(WebTools.getRealIp(getRequest()));
        createCommentRequest.setUserAgent(getHeader("User-Agent"));
        return commentService.save(createCommentRequest);
    }

    public String detail() {
        return detail(convertRequestParam(getPara()));
    }

    protected String detail(Object id) {
        Log log = new Log().findByIdOrAlias(id);
        if (log != null) {
            Integer logId = log.get("logId");
            log.put("lastLog", new Log().findLastLog(logId, I18nUtil.getStringFromRes("noLastLog")));
            log.put("nextLog", new Log().findNextLog(logId, I18nUtil.getStringFromRes("noNextLog")));
            log.put("comments", new Comment().findAllByLogId(logId));
            setAttr("log", log);
        }
        return "detail";
    }

    public String sort() {
        String typeStr = convertRequestParam(getPara(0));
        setPageInfo(Constants.getArticleUri() + "sort/" + typeStr + "-", new Log().findByTypeAlias(getParaToInt(1, 1), getDefaultRows(), typeStr), getParaToInt(1, 1));

        Type type = new Type().findByAlias(typeStr);
        setAttr("type", type);
        setAttr("tipsType", I18nUtil.getStringFromRes("category"));
        if (type != null) {
            setAttr("tipsName", type.getStr("typeName"));
        }
        return "page";
    }

    public String tag() {
        if (getPara(0) != null) {
            String tag = convertRequestParam(getPara(0));
            setPageInfo(Constants.getArticleUri() + "tag/" + getPara(0) + "-", new Log().findByTag(getParaToInt(1, 1), getDefaultRows(), tag), getParaToInt(1, 1));

            setAttr("tipsType", I18nUtil.getStringFromRes("tag"));
            setAttr("tipsName", tag);
        }
        return "page";
    }

    public String tags() {
        return "tags";
    }

    public String link() {
        return "link";
    }

    public String all() {
        int page = ParseUtil.strToInt(getPara(1), 1);
        Map<String, Object> data = new Log().find(page, getDefaultRows());
        setPageInfo(Constants.getArticleUri() + "all-", data, page);
        return "index";
    }
}
