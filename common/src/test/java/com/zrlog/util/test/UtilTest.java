package com.zrlog.util.test;

import com.zrlog.util.ZrLogUtil;
import org.junit.Test;

public class UtilTest {

    @Test
    public void testIsNormalBrowser() {
        assert ZrLogUtil.isNormalBrowser("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36");
        assert !ZrLogUtil.isNormalBrowser("Mozilla/5.0 (compatible; Baiduspider/2.0; +http://www.baidu.com/search/spider.html）");
    }
}
