package com.fzb.blog.web.incp;

import com.fzb.blog.common.BaseDataInitVO;
import com.fzb.blog.common.Constants;
import com.fzb.blog.model.*;
import com.fzb.blog.util.BlogBuildInfoUtil;
import com.fzb.blog.web.config.ZrlogConfig;
import com.fzb.blog.web.controller.BaseController;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.JFinal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class InitDataInterceptor implements Interceptor {


    private static final Logger LOGGER = LoggerFactory.getLogger(InitDataInterceptor.class);
    private static final int IDLE_TIME = 1000 * 120;
    private Timer timer = new Timer();
    private volatile long lastAccessTime;

    public InitDataInterceptor() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (lastAccessTime > 0 && System.currentTimeMillis() - lastAccessTime > IDLE_TIME) {
                    lastAccessTime = 0;
                    JFinal.me().getServletContext().removeAttribute(Constants.CACHE_KEY);
                }
            }
        }, 0, 60000);
    }

    private void doIntercept(Invocation invocation) {
        long start = System.currentTimeMillis();
        if (!ZrlogConfig.isInstalled()) {
            invocation.getController().render("/install/index.jsp");
        } else {
            if (invocation.getController() instanceof BaseController) {
                HttpServletRequest request = invocation.getController().getRequest();
                invocation.getController().setAttr("requrl", request.getRequestURL().toString());
                BaseController baseController = ((BaseController) invocation.getController());
                BaseDataInitVO vo = getCache();
                baseController.setAttr("init", vo);
                baseController.setWebSite(vo.getWebSite());
            }
        }
        invocation.invoke();
        if (BlogBuildInfoUtil.isDev()) {
            LOGGER.info(invocation.getActionKey() + " used time " + (System.currentTimeMillis() - start));
        }
    }

    private BaseDataInitVO getCache() {
        BaseDataInitVO cacheInit = (BaseDataInitVO) JFinal.me().getServletContext().getAttribute(Constants.CACHE_KEY);
        if (cacheInit == null) {
            cacheInit = new BaseDataInitVO();
            Map<String, Object> website = WebSite.dao.getWebSite();
            //兼容早期模板判断方式
            website.put("user_comment_pluginStatus", "on".equals(website.get("duoshuo_status")));
            cacheInit.setWebSite(website);
            cacheInit.setLinks(Link.dao.queryAll());
            cacheInit.setTypes(Type.dao.queryAll());
            cacheInit.setLogNavs(LogNav.dao.queryAll());
            cacheInit.setPlugins(Plugin.dao.queryAll());
            cacheInit.setArchives(Log.dao.getArchives());
            cacheInit.setTags(Tag.dao.queryAll());
            List<Type> types = cacheInit.getTypes();
            cacheInit.setHotLogs((List<Log>) Log.dao.getLogsByPage(1, 6).get("rows"));
            Map<Map<String, Object>, List<Log>> indexHotLog = new LinkedHashMap<Map<String, Object>, List<Log>>();
            for (Type type : types) {
                Map<String, Object> typeMap = new TreeMap<String, Object>();
                typeMap.put("typeName", type.getStr("typeName"));
                typeMap.put("alias", type.getStr("alias"));
                indexHotLog.put(typeMap, (List<Log>) Log.dao.getLogsBySort(1, 6, type.getStr("alias")).get("rows"));
            }
            cacheInit.setIndexHotLogs(indexHotLog);
            JFinal.me().getServletContext().setAttribute("webSite", website);
            JFinal.me().getServletContext().setAttribute(Constants.CACHE_KEY, cacheInit);
        }
        lastAccessTime = System.currentTimeMillis();
        return cacheInit;
    }

    @Override
    public void intercept(Invocation invocation) {
        doIntercept(invocation);
    }
}
