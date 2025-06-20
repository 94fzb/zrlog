package com.zrlog.admin.web.controller.page;

import com.google.gson.Gson;
import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.StringUtils;
import com.hibegin.http.server.web.Controller;
import com.zrlog.admin.business.rest.response.AdminApiPageDataStandardResponse;
import com.zrlog.admin.business.rest.response.ServerSideDataResponse;
import com.zrlog.admin.business.rest.response.UserBasicInfoResponse;
import com.zrlog.admin.web.controller.api.AdminUserController;
import com.zrlog.admin.web.token.AdminTokenThreadLocal;
import com.zrlog.blog.web.util.WebTools;
import com.zrlog.business.plugin.StaticSitePlugin;
import com.zrlog.business.rest.response.PublicInfoVO;
import com.zrlog.business.service.CommonService;
import com.zrlog.common.Constants;
import com.zrlog.common.rest.response.ApiStandardResponse;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.ZrLogUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

public class AdminPageController extends Controller {


    public void index() throws Throwable {
        if (Objects.equals(request.getUri(), Constants.ADMIN_URI_BASE_PATH) || Objects.equals(request.getUri(), Constants.ADMIN_URI_BASE_PATH + "/")) {
            response.redirect(Constants.ADMIN_URI_BASE_PATH + Constants.INDEX_URI_PATH);
            return;
        }
        renderIndex();
    }


    private void renderIndex() throws Throwable {
        InputStream inputStream = AdminPageController.class.getResourceAsStream(Constants.ADMIN_HTML_PAGE);
        if (Objects.isNull(inputStream)) {
            response.renderCode(404);
            return;
        }
        Document document = Jsoup.parse(IOUtil.getStringInputStream(inputStream));
        //clean history
        document.body().removeClass("dark");
        document.body().removeClass("light");
        Objects.requireNonNull(document.selectFirst("base")).attr("href", "/");
        document.body().addClass(Constants.getBooleanByFromWebSite("admin_darkMode") ? "dark" : "light");
        Element htmlElement = document.selectFirst("html");
        if (Objects.nonNull(htmlElement)) {
            htmlElement.attr("lang", I18nUtil.getCurrentLocale().split("_")[0]);
        }
        Elements select = document.head().select("meta[name=theme-color]");
        if (!select.isEmpty()) {
            Element first = select.first();
            if (Objects.nonNull(first)) {
                PublicInfoVO publicInfo = new CommonService().getPublicInfo(request);
                first.attr("content", publicInfo.getPwaThemeColor());
            }
        }
        String adminStaticResourceHostByWebSite = ZrLogUtil.getAdminStaticResourceBaseUrlByWebSite();

        Elements manifest = document.head().select("link[rel=manifest]");
        if (!manifest.isEmpty()) {
            Element manifestElement = manifest.first();
            if (Objects.nonNull(manifestElement)) {
                if (StringUtils.isNotEmpty(adminStaticResourceHostByWebSite) && !ZrLogUtil.isStaticPlugin(request)) {
                    manifestElement.attr("href", manifestElement.attr("href").replace("./", adminStaticResourceHostByWebSite + "/"));
                } else {
                    manifestElement.attr("href", manifestElement.attr("href").replace("./", WebTools.getHomeUrl(request)));
                }
            }
        }
        Elements scripts = document.head().select("script");
        if (!scripts.isEmpty()) {
            scripts.forEach(script -> {
                if (StringUtils.isNotEmpty(adminStaticResourceHostByWebSite) && !ZrLogUtil.isStaticPlugin(request)) {
                    script.attr("src", script.attr("src").replace("./", adminStaticResourceHostByWebSite + "/"));
                } else {
                    script.attr("src", script.attr("src").replace("./", WebTools.getHomeUrl(request)));
                }
            });
        }

        Elements base = document.head().select("base");
        base.attr("href", WebTools.getHomeUrl(request));
        ServerSideDataResponse serverSideDataResponse = serverSide(request.getUri());
        document.getElementById("__SS_DATA__").text(new Gson().toJson(serverSideDataResponse));
        document.title(serverSideDataResponse.getDocumentTitle());
        String html = document.html();
        response.renderHtmlStr(html);
        if (ZrLogUtil.isStaticPlugin(request)) {
            request.getAttr().put(StaticSitePlugin.HTML_FILE_KEY, Constants.zrLogConfig.getCacheService().saveResponseBodyToHtml(request, html));
        }
    }



    /*@ResponseBody
    public ApiStandardResponse<ServerSideDataResponse> ssJson(){
        return new ApiStandardResponse<>(serverSide(request.getParaToStr("uri")));
    }*/

    private ServerSideDataResponse serverSide(String uri) throws Throwable {
        Map<String, Object> resourceInfo = new CommonService().adminResourceInfo(request);
        if (Objects.nonNull(AdminTokenThreadLocal.getUser())) {
            UserBasicInfoResponse basicInfoResponse = new AdminUserController(request, response).index().getData();
            Method method = request.getRequestConfig().getRouter().getMethod("/api" + uri);
            try {
                Controller controller = Controller.buildController(method, request, response);
                ApiStandardResponse<Object> result = (ApiStandardResponse<Object>) method.invoke(controller);
                if (Objects.nonNull(result)) {
                    if (result instanceof AdminApiPageDataStandardResponse<?>) {
                        AdminApiPageDataStandardResponse<?> data = (AdminApiPageDataStandardResponse<?>) result;
                        return new ServerSideDataResponse(basicInfoResponse, resourceInfo, result.getData(), AdminTokenThreadLocal.getUser().getSessionId(), data.getDocumentTitle());
                    }
                    return new ServerSideDataResponse(basicInfoResponse, resourceInfo, result.getData(), AdminTokenThreadLocal.getUser().getSessionId(), Constants.getAdminDocumentTitleByUri(request.getUri()));
                } else {
                    return new ServerSideDataResponse(basicInfoResponse, resourceInfo, new Object(), AdminTokenThreadLocal.getUser().getSessionId(), Constants.getAdminDocumentTitleByUri(request.getUri()));
                }
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        } else {
            return new ServerSideDataResponse(null, resourceInfo, null, null, Constants.getAdminDocumentTitleByUri(request.getUri()));
        }
    }

    public void logout() {
        Constants.zrLogConfig.getTokenService().removeAdminToken(request, response);
    }
}
