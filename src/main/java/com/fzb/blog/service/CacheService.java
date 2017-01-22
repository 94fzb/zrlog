package com.fzb.blog.service;

import com.fzb.blog.common.BaseDataInitVO;
import com.fzb.blog.common.Constants;
import com.fzb.blog.model.*;
import com.fzb.blog.web.controller.BaseController;
import com.fzb.common.util.IOUtil;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 对缓存数据的操作
 */
public class CacheService {

    public void refreshInitDataCache(BaseController baseController) {
        cleanCache();
        updateCache(baseController);
    }

    private void cleanCache() {
        JFinal.me().getServletContext().removeAttribute(Constants.CACHE_KEY);
        IOUtil.deleteFile(PathKit.getWebRootPath() + "/post");
    }

    public boolean cleanStaticPostFileByLogId(String id) {
        Log log = Log.dao.findById(id);
        if (log != null) {
            File file = new File(PathKit.getWebRootPath() + "/post/" + id + ".html");
            boolean delete = file.delete();
            File aliasFile = new File(PathKit.getWebRootPath() + "/post/" + log.get("alias") + ".html");
            boolean deleteAlias = aliasFile.delete();
            return delete || deleteAlias;
        }
        return false;
    }

    private void updateCache(BaseController baseController) {
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
            //存放公共数据到ServletContext
            JFinal.me().getServletContext().setAttribute("webSite", website);
            JFinal.me().getServletContext().setAttribute(Constants.CACHE_KEY, cacheInit);
        }
        if (baseController != null) {
            baseController.setAttr("init", cacheInit);
            baseController.setWebSite(cacheInit.getWebSite());
        }
    }
}
