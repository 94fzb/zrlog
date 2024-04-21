package com.zrlog.test.model;

import com.zrlog.admin.web.plugin.UpdateVersionPlugin;
import com.zrlog.common.vo.Version;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.ZrLogUtil;
import org.junit.Test;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class TestCommon {

    @Test
    public void testVersionCompile() {
        boolean result = ZrLogUtil.greatThenCurrentVersion("9999999", new Date(), "1.6.4");
        System.out.println("result = " + result);
        assert result;
    }

    @Test
    public void testI18n() {
        I18nUtil.addToRequest("/", null, true);
        Map<String, Object> backend = I18nUtil.getBackend();
        System.out.println("backend = " + backend);
        assert Objects.nonNull(backend);
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

    @Test
    public void testVersionLoad() {
        Version lastVersion = new UpdateVersionPlugin().getLastVersion(true);
        System.out.println("lastVersion = " + lastVersion);
        assert Objects.nonNull(lastVersion);
    }
}
