package com.zrlog.test.model;

import com.hibegin.common.util.http.HttpUtil;
import com.hibegin.common.util.http.handle.HttpFileHandle;
import com.zrlog.test.TestApplication;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

public class TestUpgrade extends TestApplication {

    //@Test
    public void upgrade() throws IOException, URISyntaxException, InterruptedException {
        File warFile = HttpUtil.getInstance().sendGetRequest("https://dl.zrlog.com/release/zrlog.war", new HashMap<>(), new HttpFileHandle(System.getProperty("java.io.tmpdir")), new HashMap<>()).getT();
        System.out.println("warFile = " + warFile);
        //new WarUpdateVersionThread(warFile).run();
    }

    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        TestUpgrade testUpgrade = new TestUpgrade();
        testUpgrade.upgrade();
    }
}
