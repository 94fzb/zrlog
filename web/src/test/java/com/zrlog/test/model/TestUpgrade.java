package com.zrlog.test.model;

import com.hibegin.common.util.http.HttpUtil;
import com.hibegin.common.util.http.handle.HttpFileHandle;
import com.zrlog.admin.web.plugin.WarUpdateVersionThread;
import com.zrlog.test.TestApplication;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class TestUpgrade extends TestApplication {

    //@Test
    public void upgrade() throws IOException {
        File warFile = HttpUtil.getDisableRedirectInstance().sendGetRequest("http://dl.zrlog.com/preview/zrlog.war", new HashMap<String, String[]>(), new HttpFileHandle(System.getProperty("java.io.tmpdir")), new HashMap<String, String>()).getT();
        new WarUpdateVersionThread(warFile).run();
    }
}
