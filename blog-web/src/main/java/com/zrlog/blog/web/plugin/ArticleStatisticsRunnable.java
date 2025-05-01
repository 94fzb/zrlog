package com.zrlog.blog.web.plugin;

import com.hibegin.common.BaseLockObject;
import com.hibegin.common.dao.DAO;
import com.hibegin.common.util.EnvKit;
import com.hibegin.common.util.LoggerUtil;
import com.hibegin.common.util.StringUtils;
import com.zrlog.business.plugin.RequestInfo;
import com.zrlog.model.Log;
import eu.bitwalker.useragentutils.BrowserType;
import eu.bitwalker.useragentutils.UserAgent;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ArticleStatisticsRunnable extends BaseLockObject implements Runnable {

    private static final Logger LOGGER = LoggerUtil.getLogger(ArticleStatisticsRunnable.class);

    /**
     * 保留2分钟的，2分钟内不重复记数量
     */
    private static final long REMOVE_TIME = 2 * 1000 * 60L;
    private final List<RequestInfo> requestInfoList = Collections.synchronizedList(new ArrayList<>());


    private boolean isNormalBrowser(String userAgent) {
        if (StringUtils.isEmpty(userAgent)) {
            return false;
        }
        UserAgent ua = UserAgent.parseUserAgentString(userAgent);
        BrowserType browserType = ua.getBrowser().getBrowserType();
        return browserType == BrowserType.MOBILE_BROWSER || browserType == BrowserType.WEB_BROWSER;
    }

    /**
     *
     */
    private void clickAdd(Long logId) throws SQLException {
        new DAO().execute("update " + Log.TABLE_NAME + " set click = click + 1  where logId=?", logId);
    }

    @Override
    public void run() {
        lock.lock();
        try {
            List<RequestInfo> removeList = new ArrayList<>();
            for (RequestInfo requestInfo : requestInfoList) {
                if (!requestInfo.isDeal()) {
                    if (isNormalBrowser(requestInfo.getUserAgent())) {
                        clickAdd(requestInfo.getArticleId());
                    }
                    requestInfo.setDeal(true);
                }
                if (System.currentTimeMillis() - requestInfo.getRequestTime() > REMOVE_TIME) {
                    removeList.add(requestInfo);
                }
            }
            requestInfoList.removeAll(removeList);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Save error", e);
        } finally {
            lock.unlock();
        }
    }

    public void addTask(RequestInfo requestInfo) {
        requestInfoList.add(requestInfo);
        if (EnvKit.isFaaSMode()) {
            this.run();
        }
    }
}
