package com.zrlog.business.plugin;

import com.zrlog.plugin.IPlugin;

public interface ArticleStatisticsPlugin extends IPlugin {

    void record(RequestInfo requestInfo);
}
