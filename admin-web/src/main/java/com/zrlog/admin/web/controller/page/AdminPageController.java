package com.zrlog.admin.web.controller.page;

import com.google.gson.Gson;
import com.hibegin.common.util.IOUtil;
import com.hibegin.http.server.web.Controller;
import com.hibegin.http.server.web.cookie.Cookie;
import com.zrlog.admin.web.controller.api.AdminUserController;
import com.zrlog.admin.web.token.AdminTokenService;
import com.zrlog.admin.web.token.AdminTokenThreadLocal;
import com.zrlog.business.service.CommonService;
import com.zrlog.common.Constants;
import com.zrlog.util.I18nUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.InputStream;
import java.util.Date;
import java.util.Objects;

public class AdminPageController extends Controller {

    public void index() {
        if (getRequest().getUri().endsWith(Constants.ADMIN_URI_BASE_PATH) || getRequest().getUri().endsWith(Constants.ADMIN_URI_BASE_PATH + "/")) {
            response.redirect(Constants.ADMIN_URI_BASE_PATH + Constants.INDEX_URI_PATH);
            return;
        }
        renderIndex();
    }

    private void renderIndex() {
        InputStream inputStream = AdminPageController.class.getResourceAsStream(Constants.ADMIN_HTML_PAGE);
        if (Objects.isNull(inputStream)) {
            response.renderCode(404);
            return;
        }
        Document document = Jsoup.parse(IOUtil.getStringInputStream(inputStream));
        //clean history
        document.body().removeClass("dark");
        document.body().removeClass("light");
        document.selectFirst("base").attr("href", "/");
        document.body().addClass(Constants.getBooleanByFromWebSite("admin_darkMode") ? "dark" : "light");
        document.title(Constants.WEB_SITE.get("title") + " | " + I18nUtil.getBlogStringFromRes("admin.management"));
        document.getElementById("resourceInfo").text(new Gson().toJson(new CommonService().blogResourceInfo()));
        if(Objects.nonNull(AdminTokenThreadLocal.getUser())){
            document.getElementById("userInfo").text(new Gson().toJson(new AdminUserController(request,response).index()));
        }

        response.renderHtmlStr(document.html());
    }

    public void logout() {
        Cookie[] cookies = getRequest().getCookies();
        for (Cookie cookie : cookies) {
            if (AdminTokenService.ADMIN_TOKEN.equals(cookie.getName())) {
                cookie.setValue("");
                cookie.setExpireDate(new Date(0));
                cookie.setPath("/");
                cookie.setHttpOnly(true);
                getResponse().addCookie(cookie);
            }
        }
        response.redirect(Constants.ADMIN_LOGIN_URI_PATH);
    }
}
