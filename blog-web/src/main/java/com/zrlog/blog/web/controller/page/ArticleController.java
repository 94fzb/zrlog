package com.zrlog.blog.web.controller.page;

import com.hibegin.common.util.StringUtils;
import com.jfinal.core.Controller;
import com.zrlog.admin.web.token.AdminTokenService;
import com.zrlog.blog.web.util.WebTools;
import com.zrlog.business.cache.CacheService;
import com.zrlog.business.rest.request.CreateCommentRequest;
import com.zrlog.business.rest.response.CreateCommentResponse;
import com.zrlog.business.service.CommentService;
import com.zrlog.business.service.VisitorArticleService;
import com.zrlog.business.util.PagerUtil;
import com.zrlog.common.Constants;
import com.zrlog.common.rest.request.PageRequest;
import com.zrlog.common.vo.AdminTokenVO;
import com.zrlog.data.dto.PageData;
import com.zrlog.model.Comment;
import com.zrlog.model.Log;
import com.zrlog.model.Type;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.ParseUtil;
import com.zrlog.util.ZrLogUtil;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.math.BigDecimal;

public class ArticleController extends Controller {

    private final VisitorArticleService visitorArticleService = new VisitorArticleService();

    private final CommentService commentService = new CommentService();

    private final AdminTokenService adminTokenService = new AdminTokenService();

    /**
     * add page info for template more easy
     */
    private void setPageDataInfo(String currentUri, PageData<Log> data, PageRequest pageRequest) {
        setAttr("yurl", currentUri);
        long totalPage =
                BigDecimal.valueOf(Math.ceil(data.getTotalElements() * 1.0 / pageRequest.getSize())).longValue();
        if (totalPage > 0) {
            setAttr("data", data);
            //大于1页
            if (totalPage > 1) {
                setAttr("pager", PagerUtil.generatorPager(currentUri, pageRequest.getPage(), totalPage));
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
        PageData<Log> data;
        if (getParaToInt(1) == null) {
            if (StringUtils.isNotEmpty(getPara("key"))) {
                if ("GET".equals(getRequest().getMethod())) {
                    key = WebTools.convertRequestParam(getPara("key"));
                } else {
                    key = getPara("key");
                }
                data = visitorArticleService.pageByKeywords(new PageRequest(1, Constants.getDefaultRows()), key);
            } else {
                return all();
            }

        } else {
            key = WebTools.convertRequestParam(getPara(0));
            data = visitorArticleService.pageByKeywords(new PageRequest(getParaToInt(1), Constants.getDefaultRows()),
                    key);
        }
        // 记录回话的Key
        setAttr("key", WebTools.htmlEncode(key));

        setAttr("tipsType", I18nUtil.getBlogStringFromRes("search"));
        setAttr("tipsName", WebTools.htmlEncode(key));

        setPageDataInfo(Constants.getArticleUri() + "search/" + key + "-", data, new PageRequest(getParaToInt(1, 1),
                Constants.getDefaultRows()));
        return "page";
    }

    public String record() {
        setAttr("tipsType", I18nUtil.getBlogStringFromRes("archive"));
        setAttr("tipsName", getPara(0));

        setPageDataInfo(Constants.getArticleUri() + "record/" + getPara(0) + "-", new Log().findByDate(getParaToInt(1
                , 1), Constants.getDefaultRows(), getPara(0)), new PageRequest(getParaToInt(1, 1),
                Constants.getDefaultRows()));
        return "page";
    }

    public void addComment() {
        CreateCommentResponse response = saveComment();
        String ext = "";
        if (Constants.isStaticHtmlStatus()) {
            ext = ".html";
            new CacheService().refreshInitDataCache(this.getRequest(), true);
        }
        redirect("/" + Constants.getArticleUri() + response.getAlias() + ext);
    }

    public CreateCommentResponse saveComment() {
        CreateCommentRequest createCommentRequest = ZrLogUtil.convertRequestParam(getRequest().getParameterMap(),
                CreateCommentRequest.class);
        createCommentRequest.setIp(WebTools.getRealIp(getRequest()));
        createCommentRequest.setUserAgent(Jsoup.clean(getHeader("User-Agent"), Whitelist.basic()));
        return commentService.save(createCommentRequest);
    }

    public String detail() {
        return detail(WebTools.convertRequestParam(getPara()));
    }

    protected String detail(Object idOrAlias) {
        AdminTokenVO adminTokenVO = adminTokenService.getAdminTokenVO(getRequest());
        Log log;
        if (adminTokenVO == null) {
            log = new Log().findByIdOrAlias(idOrAlias);
        } else {
            log = new Log().adminFindByIdOrAlias(idOrAlias);
        }
        if (log != null) {
            Integer logId = log.get("logId");
            log.put("lastLog", new Log().findLastLog(logId, I18nUtil.getBlogStringFromRes("noLastLog")));
            log.put("nextLog", new Log().findNextLog(logId, I18nUtil.getBlogStringFromRes("noNextLog")));
            log.put("comments", new Comment().findAllByLogId(logId));
            setAttr("log", log);
            return "detail";
        }
        return "index";
    }

    public String sort() {
        String typeStr = WebTools.convertRequestParam(getPara(0));
        setPageDataInfo(Constants.getArticleUri() + "sort/" + typeStr + "-", new Log().findByTypeAlias(getParaToInt(1
                , 1), Constants.getDefaultRows(), typeStr), new PageRequest(getParaToInt(1, 1),
                Constants.getDefaultRows()));

        Type type = new Type().findByAlias(typeStr);
        setAttr("type", type);
        setAttr("tipsType", I18nUtil.getBlogStringFromRes("category"));
        if (type != null) {
            setAttr("tipsName", type.getStr("typeName"));
        }
        return "page";
    }

    public String tag() {
        if (getPara(0) != null) {
            String tag = WebTools.convertRequestParam(getPara(0));
            setPageDataInfo(Constants.getArticleUri() + "tag/" + getPara(0) + "-", new Log().findByTag(getParaToInt(1
                    , 1), Constants.getDefaultRows(), tag), new PageRequest(getParaToInt(1, 1),
                    Constants.getDefaultRows()));

            setAttr("tipsType", I18nUtil.getBlogStringFromRes("tag"));
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
        PageRequest pageRequest = new PageRequest(ParseUtil.strToInt(getPara(1), 1), Constants.getDefaultRows());
        PageData<Log> data = new Log().visitorFind(pageRequest, null);
        setPageDataInfo(Constants.getArticleUri() + "all-", data, pageRequest);
        return "index";
    }
}
