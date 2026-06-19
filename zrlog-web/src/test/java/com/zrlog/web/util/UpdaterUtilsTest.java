package com.zrlog.web.util;

import com.zrlog.common.Updater;
import com.zrlog.common.UpdaterTypeEnum;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UpdaterUtilsTest {

    @Test
    public void shouldCreateZipUpdaterForStandardJvmRuntime() {
        Updater updater = UpdaterUtils.getUpdater(new String[0], null);

        assertNotNull(updater);
        assertEquals(UpdaterTypeEnum.ZIP, updater.getType());
    }
}
