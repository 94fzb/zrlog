package com.zrlog.admin.web.controller.page;

import com.google.gson.Gson;
import com.hibegin.common.util.IOUtil;
import com.hibegin.http.annotation.ResponseBody;
import com.hibegin.http.server.web.Controller;
import com.hibegin.http.server.web.cookie.Cookie;
import com.zrlog.admin.business.rest.response.ServerSideDataResponse;
import com.zrlog.admin.business.rest.response.UserBasicInfoResponse;
import com.zrlog.admin.web.controller.api.AdminUserController;
import com.zrlog.admin.web.token.AdminTokenService;
import com.zrlog.admin.web.token.AdminTokenThreadLocal;
import com.zrlog.business.service.CommonService;
import com.zrlog.common.Constants;
import com.zrlog.common.rest.response.ApiStandardResponse;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.ZrLogUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;
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
        document.getElementById("__SS_DATA__").text(new Gson().toJson(serverSide(request.getUri())));
        response.renderHtmlStr(document.html());
    }

    /*@ResponseBody
    public ApiStandardResponse<ServerSideDataResponse> ssJson(){
        return new ApiStandardResponse<>(serverSide(request.getParaToStr("uri")));
    }*/

    private ServerSideDataResponse serverSide(String uri){
        Map<String,Object> resourceInfo = new CommonService().blogResourceInfo();
        if(Objects.nonNull(AdminTokenThreadLocal.getUser())){
            UserBasicInfoResponse basicInfoResponse =  new AdminUserController(request,response).index().getData();
            try{
                Method method = request.getRequestConfig().getRouter().getMethod("/api" +uri);
                Controller controller = ZrLogUtil.buildController(method,request,response);
                ApiStandardResponse<Object> result = (ApiStandardResponse<Object>) method.invoke(controller);
                if(Objects.nonNull(result)){
                    return new ServerSideDataResponse(basicInfoResponse,resourceInfo,result.getData(),AdminTokenThreadLocal.getUser().getSessionId());
                } else{
                    return new ServerSideDataResponse(basicInfoResponse,resourceInfo,new Object(),AdminTokenThreadLocal.getUser().getSessionId());
                }
            } catch (Exception e){
                throw new RuntimeException(e);
            }
        } else{
            return new ServerSideDataResponse(null,resourceInfo,null,null);
        }
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
