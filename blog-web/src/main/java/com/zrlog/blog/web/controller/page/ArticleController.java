package com.zrlog.blog.web.controller.page;

import com.hibegin.common.util.LoggerUtil;
import com.hibegin.common.util.StringUtils;
import com.hibegin.http.server.web.Controller;
import com.zrlog.blog.web.util.WebTools;
import com.zrlog.business.rest.request.CreateCommentRequest;
import com.zrlog.business.rest.response.CreateCommentResponse;
import com.zrlog.business.service.ArticleService;
import com.zrlog.business.service.CommentService;
import com.zrlog.business.service.VisitorArticleService;
import com.zrlog.business.util.PagerUtil;
import com.zrlog.common.Constants;
import com.zrlog.common.rest.request.PageRequest;
import com.zrlog.common.rest.request.PageRequestImpl;
import com.zrlog.data.dto.PageData;
import com.zrlog.model.Log;
import com.zrlog.model.Type;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.ParseUtil;
import com.zrlog.util.ZrLogUtil;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

public class ArticleController extends Controller {

    private static final Logger LOGGER = LoggerUtil.getLogger(ArticleController.class);
    private final VisitorArticleService visitorArticleService = new VisitorArticleService();

    private final CommentService commentService = new CommentService();
    private final ArticleService articleService = new ArticleService();

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
        PageRequest pageRequest = new PageRequestImpl(parseUriInfo(request.getUri()).page(), Constants.getDefaultRows());
        PageData<Map<String, Object>> data = new Log().visitorFind(pageRequest, null);
        setPageDataInfo(Constants.getArticleUri() + "all-", data, pageRequest);
        return "index";
    }


    public String search() throws SQLException {
        String key = request.getParaToStr("key");
        ArticleUriInfoVO uriInfoVO = parseUriInfo(request.getUri());
        PageData<Map<String, Object>> data;
        if (StringUtils.isNotEmpty(key)) {
            if ("GET".equals(getRequest().getMethod().name())) {
                key = WebTools.convertRequestParam(key);
            }
        } else {
            try {
                key = uriInfoVO.key();
            } catch (Exception e) {
                LOGGER.warning("Parse " + request.getUri() + " error " + e.getMessage());
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

        setPageDataInfo(Constants.getArticleUri() + "search/" + key + "-", data, new PageRequestImpl(uriInfoVO.page(), Constants.getDefaultRows()));
        return "page";
    }

    public String record() {
        ArticleUriInfoVO uriInfoVO = parseUriInfo(request.getUri());
        request.getAttr().put("tipsType", I18nUtil.getBlogStringFromRes("archive"));
        request.getAttr().put("tipsName", uriInfoVO.key());

        setPageDataInfo(Constants.getArticleUri() + "record/" + uriInfoVO.key() + "-", new Log().findByDate(uriInfoVO.page(), Constants.getDefaultRows(),
                uriInfoVO.key()), new PageRequestImpl(uriInfoVO.page(),
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
        Map<String, Object> detail = articleService.detail(uri.replace("/", "").replace(".html", ""));
        if (Objects.nonNull(detail)) {
            request.getAttr().put("log", detail);
        }
        return "detail";
    }


    private static ArticleUriInfoVO parseUriInfo(String uri) {
        String rawUrl = uri;
        if (rawUrl.endsWith(".html")) {
            rawUrl = rawUrl.substring(0, rawUrl.length() - ".html".length());
        }
        String rawArgs = rawUrl.substring(rawUrl.lastIndexOf("/") + 1);
        List<String> list = Arrays.asList(rawArgs.split("-"));
        boolean numeric = ParseUtil.isNumeric(list.getLast());
        StringJoiner sj = new StringJoiner("-");
        int page = 1;
        if (numeric) {
            page = ParseUtil.strToInt(list.getLast(), 1);
            for (int i = 0; i < list.size() - 1; i++) {
                sj.add(list.get(i));
            }
        } else {
            for (String arg : list) {
                sj.add(arg);
            }
        }
        String key = sj.toString();
        return new ArticleUriInfoVO(WebTools.convertRequestParam(key), page);
    }

    public static void main(String[] args) {
        ArticleUriInfoVO uriInfoVO = parseUriInfo("/record/2015-06.html");
        System.out.println(uriInfoVO);
    }

    public String sort() throws SQLException {
        ArticleUriInfoVO uriInfoVO = parseUriInfo(request.getUri());
        setPageDataInfo(Constants.getArticleUri() + "sort/" + uriInfoVO.key() + "-", new Log().findByTypeAlias(uriInfoVO.page(), Constants.getDefaultRows(),
                uriInfoVO.key()), new PageRequestImpl(uriInfoVO.page(),
                Constants.getDefaultRows()));

        Map<String, Object> type = new Type().findByAlias(uriInfoVO.key());
        request.getAttr().put("type", type);
        request.getAttr().put("tipsType", I18nUtil.getBlogStringFromRes("category"));
        if (type != null) {
            request.getAttr().put("tipsName", type.get("typeName"));
        }
        return "page";
    }

    public String tag() throws SQLException {
        ArticleUriInfoVO uriInfoVO = parseUriInfo(request.getUri());

        String tag = WebTools.convertRequestParam(uriInfoVO.key());
        setPageDataInfo(Constants.getArticleUri() + "tag/" + tag + "-", new Log().findByTag(uriInfoVO.page(), Constants.getDefaultRows(), tag),
                new PageRequestImpl(uriInfoVO.page(),
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
