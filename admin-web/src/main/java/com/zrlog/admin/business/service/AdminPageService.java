package com.zrlog.admin.business.service;

import com.google.gson.Gson;
import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.StringUtils;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.hibegin.http.server.web.Controller;
import com.zrlog.admin.business.rest.response.AdminApiPageDataStandardResponse;
import com.zrlog.admin.business.rest.response.ServerSideDataResponse;
import com.zrlog.admin.business.rest.response.UserBasicInfoResponse;
import com.zrlog.admin.web.controller.api.AdminUserController;
import com.zrlog.admin.web.token.AdminTokenThreadLocal;
import com.zrlog.blog.web.util.WebTools;
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

public class AdminPageService {

    public String buildHtml(HttpRequest request, HttpResponse httpResponse, InputStream htmlInputStream) throws Throwable {
        Document document = Jsoup.parse(IOUtil.getStringInputStream(htmlInputStream));
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
        String adminStaticResourceHostByWebSite = ZrLogUtil.getAdminStaticResourceBaseUrlByWebSite(request);

        Elements manifest = document.head().select("link[rel=manifest]");
        if (!manifest.isEmpty()) {
            Element manifestElement = manifest.first();
            if (Objects.nonNull(manifestElement)) {
                //login page, disable pwa
                if (request.getUri().startsWith(Constants.ADMIN_LOGIN_URI_PATH)) {
                    manifest.remove();
                } else {
                    String newUrl;
                    if (StringUtils.isNotEmpty(adminStaticResourceHostByWebSite) && !ZrLogUtil.isStaticPlugin(request)) {
                        newUrl = manifestElement.attr("href").replaceFirst("/", adminStaticResourceHostByWebSite + "/");
                    } else {
                        newUrl = manifestElement.attr("href").replaceFirst("/", WebTools.getHomeUrl(request));
                    }
                    manifestElement.attr("href", newUrl + "?v=" + Constants.zrLogConfig.getAdminResource().getStaticResourceBuildId());
                }
            }
        }
        Elements scripts = document.head().select("script");
        if (!scripts.isEmpty()) {
            scripts.forEach(script -> {
                if (StringUtils.isNotEmpty(adminStaticResourceHostByWebSite) && !ZrLogUtil.isStaticPlugin(request)) {
                    script.attr("src", script.attr("src").replaceFirst("/", adminStaticResourceHostByWebSite + "/"));
                } else {
                    script.attr("src", script.attr("src").replaceFirst("/", WebTools.getHomeUrl(request)));
                }
            });
        }

        Elements base = document.head().select("base");
        base.attr("href", WebTools.getHomeUrl(request));
        ServerSideDataResponse<Object> serverSideDataResponse = serverSide(request.getUri(), request, httpResponse);
        Objects.requireNonNull(document.getElementById("__SS_DATA__")).text(new Gson().toJson(serverSideDataResponse));
        document.title(serverSideDataResponse.getDocumentTitle());
        return document.html();
    }

    public ServerSideDataResponse<Object> serverSide(String uri, HttpRequest request, HttpResponse response) throws Throwable {
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
                        return new ServerSideDataResponse<>(basicInfoResponse, resourceInfo, result.getData(), AdminTokenThreadLocal.getUser().getSessionId(), data.getDocumentTitle());
                    }
                    return new ServerSideDataResponse<>(basicInfoResponse, resourceInfo, result.getData(), AdminTokenThreadLocal.getUser().getSessionId(), Constants.getAdminDocumentTitleByUri(request.getUri()));
                } else {
                    return new ServerSideDataResponse<>(basicInfoResponse, resourceInfo, new Object(), AdminTokenThreadLocal.getUser().getSessionId(), Constants.getAdminDocumentTitleByUri(request.getUri()));
                }
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        } else {
            return new ServerSideDataResponse<>(null, resourceInfo, null, null, Constants.getAdminDocumentTitleByUri(request.getUri()));
        }
    }

}
