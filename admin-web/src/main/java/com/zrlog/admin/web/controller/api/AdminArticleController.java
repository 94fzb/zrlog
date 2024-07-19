package com.zrlog.admin.web.controller.api;

import com.hibegin.common.util.BeanUtil;
import com.hibegin.common.util.StringUtils;
import com.hibegin.http.annotation.ResponseBody;
import com.hibegin.http.server.web.Controller;
import com.zrlog.admin.business.exception.ArgsException;
import com.zrlog.admin.business.exception.NotFindDbEntryException;
import com.zrlog.admin.business.exception.PermissionErrorException;
import com.zrlog.admin.business.rest.request.CreateArticleRequest;
import com.zrlog.admin.business.rest.request.UpdateArticleRequest;
import com.zrlog.admin.business.rest.response.*;
import com.zrlog.admin.business.service.AdminArticleService;
import com.zrlog.admin.web.annotation.RefreshCache;
import com.zrlog.admin.web.controller.page.AdminPageController;
import com.zrlog.admin.web.token.AdminTokenThreadLocal;
import com.zrlog.blog.web.util.ControllerUtil;
import com.zrlog.blog.web.util.WebTools;
import com.zrlog.business.rest.response.ArticleResponseEntry;
import com.zrlog.business.service.TemplateHelper;
import com.zrlog.common.Constants;
import com.zrlog.common.rest.response.ApiStandardResponse;
import com.zrlog.data.dto.PageData;
import com.zrlog.model.Log;
import com.zrlog.model.Tag;
import com.zrlog.model.Type;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.ZrLogUtil;

import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

public class AdminArticleController extends Controller {

    private final AdminArticleService articleService = new AdminArticleService();

    @RefreshCache(async = true)
    @ResponseBody
    public ApiStandardResponse<DeleteLogResponse> delete() throws SQLException {
        if (ZrLogUtil.isPreviewMode()) {
            throw new PermissionErrorException();
        }
        String[] ids = getRequest().getParaToStr("id").split(",");
        for (String id : ids) {
            articleService.delete(Long.valueOf(id));
        }
        return new ApiStandardResponse<>(new DeleteLogResponse(true));
    }

    @RefreshCache(async = true)
    @ResponseBody
    public ApiStandardResponse<CreateOrUpdateArticleResponse> create() {
        CreateOrUpdateArticleResponse create = articleService.create(AdminTokenThreadLocal.getUser(), BeanUtil.convertWithValid(getRequest().getInputStream(),
                CreateArticleRequest.class));
        create.setPreviewUrl(getPreviewUrl(create));
        return new ApiStandardResponse<>(create);
    }

    @RefreshCache(async = true)
    @ResponseBody
    public ApiStandardResponse<CreateOrUpdateArticleResponse> update() {
        CreateOrUpdateArticleResponse update = articleService.update(AdminTokenThreadLocal.getUser(), BeanUtil.convertWithValid(getRequest().getInputStream(),
                UpdateArticleRequest.class));
        update.setPreviewUrl(getPreviewUrl(update));
        return new ApiStandardResponse<>(update);
    }

    @ResponseBody
    public AdminApiPageDataStrandardResponse<PageData<ArticleResponseEntry>> index() throws SQLException {
        String key = WebTools.convertRequestParam(request.getParaToStr("key"));
        PageData<ArticleResponseEntry> pageData = articleService.adminPage(ControllerUtil.getPageRequest(this), key, request);
        pageData.setKey(key);
        AdminApiPageDataStrandardResponse<PageData<ArticleResponseEntry>> strandardResponse = new AdminApiPageDataStrandardResponse<>(pageData);
        strandardResponse.setDocumentTitle(Constants.getAdminTitle(I18nUtil.getAdminStringFromRes("blogManage")));
        return strandardResponse;
    }


    private String getPreviewUrl(CreateOrUpdateArticleResponse articleResponseEntry) {
        String key = articleResponseEntry.getLogId() + "";
        if (StringUtils.isNotEmpty(articleResponseEntry.getAlias())) {
            key = articleResponseEntry.getAlias();
        }
        if (articleResponseEntry.getPrivacy() || articleResponseEntry.getRubbish()) {
            return "/admin/403?message=" + I18nUtil.getBackendStringFromRes("preview403");
        }
        return ZrLogUtil.getHomeUrlWithHost(getRequest()) + Constants.getArticleUri() + key + TemplateHelper.getSuffix(request);
    }

    @ResponseBody
    public AdminApiPageDataStrandardResponse<ArticleGlobalResponse> articleEdit() throws SQLException {
        ArticleGlobalResponse response = new ArticleGlobalResponse();
        response.setTags(new Tag().findAll());
        response.setTypes(new Type().findAll());
        String id = request.getParaToStr("id");
        if (StringUtils.isNotEmpty(id)) {
            response.setArticle(detail().getData());
        } else {
            response.setArticle(new LoadEditArticleResponse());
        }
        AdminApiPageDataStrandardResponse<ArticleGlobalResponse> strandardResponse = new AdminApiPageDataStrandardResponse<>(response);
        StringJoiner sj = new StringJoiner(Constants.ADMIN_TITLE_CHAR);
        if (Objects.nonNull(response.getArticle().getTitle())) {
            sj.add(response.getArticle().getTitle());
        }
        sj.add(I18nUtil.getAdminStringFromRes("admin.log.edit"));
        strandardResponse.setDocumentTitle(Constants.getAdminTitle(sj.toString()));
        return strandardResponse;
    }

    /**
     * 仅保留，便于测试
     *
     * @return
     * @throws SQLException
     */
    @ResponseBody
    public ApiStandardResponse<LoadEditArticleResponse> detail() throws SQLException {
        String id = getRequest().getParaToStr("id");
        if (StringUtils.isEmpty(id)) {
            throw new ArgsException("id");
        }
        Map<String, Object> log = new Log().adminFindByIdOrAlias(id);
        if (log == null) {
            throw new NotFindDbEntryException();
        }
        log.remove("releaseTime");
        log.remove("last_update_date");
        log.remove("lastUpdateDate");
        return new ApiStandardResponse<>(BeanUtil.convert(log, LoadEditArticleResponse.class));
    }

}
