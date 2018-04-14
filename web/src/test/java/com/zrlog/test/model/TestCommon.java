package com.zrlog.test.model;

import com.zrlog.util.ZrLogUtil;
import org.junit.Test;

import java.util.Date;

public class TestCommon {

    @Test
    public void testVersionCompile() {
        boolean result = ZrLogUtil.greatThenCurrentVersion("9999999", new Date(), "1.6.4");
        System.out.println("result = " + result);
        assert result;
    }

    @Test
    public void ipTest() {
        assert ZrLogUtil.isInternalHostName("127.0.0.1");
        assert ZrLogUtil.isInternalHostName("127.0.0.2");
        assert ZrLogUtil.isInternalHostName("192.168.0.1");
        assert ZrLogUtil.isInternalHostName("172.16.0.1");
        assert ZrLogUtil.isInternalHostName("10.0.0.1");
        assert !ZrLogUtil.isInternalHostName("www.baidu.com");
    }
}
