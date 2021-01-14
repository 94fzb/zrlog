package com.zrlog.web.controller.admin.page;

import com.hibegin.common.util.IOUtil;
import com.jfinal.kit.PathKit;
import com.jfinal.render.HtmlRender;
import com.zrlog.common.Constants;
import com.zrlog.web.WebConstants;
import com.zrlog.web.controller.BaseController;
import com.zrlog.web.token.AdminTokenService;

import javax.servlet.http.Cookie;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class AdminPageController extends BaseController {

    private final AdminTokenService adminTokenService = new AdminTokenService();

    public void index() throws FileNotFoundException {
        if (getRequest().getRequestURI().endsWith(Constants.ADMIN_URI_BASE_PATH) || getRequest().getRequestURI().endsWith(Constants.ADMIN_URI_BASE_PATH + "/")) {
            redirect(Constants.ADMIN_URI_BASE_PATH + "/index");
            return;
        }
        render(new HtmlRender(IOUtil.getStringInputStream(new FileInputStream(PathKit.getWebRootPath() + "/admin/index.html"))));
    }

    public void login() throws FileNotFoundException {
        render(new HtmlRender(IOUtil.getStringInputStream(new FileInputStream(PathKit.getWebRootPath() + "/admin/index.html"))));
    }

    public void logout() {
        Cookie[] cookies = getRequest().getCookies();
        for (Cookie cookie : cookies) {
            if (WebConstants.ADMIN_TOKEN.equals(cookie.getName())) {
                cookie.setValue("");
                cookie.setMaxAge(Constants.getSessionTimeout().intValue());
                cookie.setPath("/");
                adminTokenService.setCookieDomain(getRequest(), cookie);
                getResponse().addCookie(cookie);
            }
        }
        redirect(Constants.ADMIN_LOGIN_URI_PATH);
    }
}
