package com.zrlog.blog.web.controller.page;

import com.hibegin.common.util.LoggerUtil;
import com.hibegin.common.util.StringUtils;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.hibegin.http.server.web.Controller;
import com.zrlog.blog.web.util.WebTools;
import com.zrlog.business.cache.CacheServiceImpl;
import com.zrlog.business.rest.request.CreateCommentRequest;
import com.zrlog.business.rest.response.CreateCommentResponse;
import com.zrlog.business.service.CommentService;
import com.zrlog.business.service.VisitorArticleService;
import com.zrlog.business.util.PagerUtil;
import com.zrlog.common.Constants;
import com.zrlog.common.ZrLogConfig;
import com.zrlog.common.rest.request.PageRequest;
import com.zrlog.common.rest.request.PageRequestImpl;
import com.zrlog.data.dto.PageData;
import com.zrlog.model.Comment;
import com.zrlog.model.Log;
import com.zrlog.model.Type;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.ParseUtil;
import com.zrlog.util.ZrLogUtil;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

public class ArticleController extends Controller {

    private final VisitorArticleService visitorArticleService = new VisitorArticleService();

    private final CommentService commentService = new CommentService();

    public ArticleController() {
    }

    public ArticleController(HttpRequest request, HttpResponse response) {
        super(request, response);
    }

    /**
     * add page info for template more easy
     */
    private void setPageDataInfo(String currentUri, PageData<Map<String, Object>> data, PageRequest pageRequest) {
        getRequest().getAttr().put("yurl", currentUri);
        long totalPage =
                BigDecimal.valueOf(Math.ceil(data.getTotalElements() * 1.0 / pageRequest.getSize())).longValue();
        if (totalPage > 0) {
            getRequest().getAttr().put("data", data);
            //大于1页
            if (totalPage > 1) {
                getRequest().getAttr().put("pager", PagerUtil.generatorPager(currentUri, pageRequest.getPage(), totalPage));
            }
        }
    }

    public String index() throws SQLException {
        long page = getPage();

        PageRequest pageRequest = new PageRequestImpl(page, Constants.getDefaultRows());
        PageData<Map<String, Object>> data = new Log().visitorFind(pageRequest, null);
        setPageDataInfo(Constants.getArticleUri() + "all-", data, pageRequest);
        return "index";
    }

    private long getPage() {
        int page = 1;
        if (getRequest().getUri().contains("-")) {
            page = ParseUtil.strToInt(getRequest().getUri().split("-")[1].replace(".html", ""), 1);
        }
        return page;
    }

    public String search() throws SQLException {
        String key = request.getParaToStr("key");
        PageData<Map<String, Object>> data;
        if (StringUtils.isNotEmpty(key)) {
            if ("GET".equals(getRequest().getMethod().name())) {
                key = WebTools.convertRequestParam(key);
            }
        } else {
            try {
                key = parseArgs(request.getUri(), 2, 0);
            } catch (Exception e) {
                LoggerUtil.getLogger(ArticleController.class).log(Level.WARNING, "Parse " + request.getUri() + " error " + e.getMessage());
            }
        }
        if (StringUtils.isEmpty(key)) {
            return index();
        }
        data = visitorArticleService.pageByKeywords(new PageRequestImpl(1L, Constants.getDefaultRows()), key);
        // 记录回话的Key
        request.getAttr().put("key", WebTools.htmlEncode(key));

        request.getAttr().put("tipsType", I18nUtil.getBlogStringFromRes("search"));
        request.getAttr().put("tipsName", WebTools.htmlEncode(key));

        setPageDataInfo(Constants.getArticleUri() + "search/" + key + "-", data, new PageRequestImpl(getPage(),
                Constants.getDefaultRows()));
        return "page";
    }

    public String record() {
        String dateStr = parseArgs(request.getUri(), 2, 0);

        request.getAttr().put("tipsType", I18nUtil.getBlogStringFromRes("archive"));
        request.getAttr().put("tipsName", dateStr);

        setPageDataInfo(Constants.getArticleUri() + "record/" + dateStr + "-", new Log().findByDate(getPage(), Constants.getDefaultRows(),
                dateStr), new PageRequestImpl(getPage(),
                Constants.getDefaultRows()));
        return "page";
    }

    public void addComment() throws SQLException {
        CreateCommentResponse x = saveComment();
        String ext = "";
        if (Constants.isStaticHtmlStatus()) {
            ext = ".html";
            Constants.zrLogConfig.getCacheService().refreshInitDataCacheAsync(this.getRequest(), true).join();
        }
        response.redirect("/" + Constants.getArticleUri() + x.getAlias() + ext);
    }

    public CreateCommentResponse saveComment() throws SQLException {
        CreateCommentRequest createCommentRequest = ZrLogUtil.convertRequestParam(getRequest().decodeParamMap(),
                CreateCommentRequest.class);
        createCommentRequest.setIp(WebTools.getRealIp(getRequest()));
        createCommentRequest.setUserAgent(Jsoup.clean(request.getHeader("User-Agent"), Safelist.basic()));
        return commentService.save(createCommentRequest);
    }

    public String detail() throws SQLException {
        String uri = getRequest().getUri();
        return detail(uri.replace("/", "").replace(".html", ""));
    }

    private String detail(Object idOrAlias) throws SQLException {
        Map<String, Object> log = new Log().findByIdOrAlias(idOrAlias);
        if (log != null) {
            Integer logId = (Integer) log.get("logId");
            Map<String, Object> lastLog = Objects.requireNonNullElse(new Log().findLastLog(logId), new HashMap<>(Map.of("title", I18nUtil.getBlogStringFromRes("noLastLog"), "alias", idOrAlias)));
            Map<String, Object> nextLog = Objects.requireNonNullElse(new Log().findNextLog(logId), new HashMap<>(Map.of("title", I18nUtil.getBlogStringFromRes("noNextLog"), "alias", idOrAlias)));
            log.put("lastLog", lastLog);
            log.put("nextLog", nextLog);
            log.put("comments", new Comment().findAllByLogId(logId));
            getRequest().getAttr().put("log", log);
            return "detail";
        }
        return "index";
    }

    private static String parseArgs(String uri, Integer idx, Integer args) {
        return WebTools.convertRequestParam(uri.split("/")[idx].split("-")[args]).replace(".html", "");
    }

    public String sort() throws SQLException {
        String typeStr = parseArgs(request.getUri(), 2, 0);
        setPageDataInfo(Constants.getArticleUri() + "sort/" + typeStr + "-", new Log().findByTypeAlias(getPage(), Constants.getDefaultRows(), typeStr), new PageRequestImpl(getPage(),
                Constants.getDefaultRows()));

        Map<String, Object> type = new Type().findByAlias(typeStr);
        request.getAttr().put("type", type);
        request.getAttr().put("tipsType", I18nUtil.getBlogStringFromRes("category"));
        if (type != null) {
            request.getAttr().put("tipsName", type.get("typeName"));
        }
        return "page";
    }

    public String tag() throws SQLException {
        String tag = WebTools.convertRequestParam(parseArgs(request.getUri(), 2, 0));
        setPageDataInfo(Constants.getArticleUri() + "tag/" + tag + "-", new Log().findByTag(getPage(), Constants.getDefaultRows(), tag), new PageRequestImpl(getPage(),
                Constants.getDefaultRows()));
        getRequest().getAttr().put("tipsType", I18nUtil.getBlogStringFromRes("tag"));
        getRequest().getAttr().put("tipsName", tag);
        return "page";
    }

    public String tags() {
        return "tags";
    }

    public String link() {
        return "link";
    }

}
