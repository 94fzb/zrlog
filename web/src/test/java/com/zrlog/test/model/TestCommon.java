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
}
