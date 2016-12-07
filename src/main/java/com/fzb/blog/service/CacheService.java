package com.fzb.blog.service;

import com.fzb.blog.common.Constants;
import com.fzb.blog.model.Log;
import com.fzb.common.util.IOUtil;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;

import java.io.File;

public class CacheService {

    public void cleanCache() {
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
}
