package com.zrlog.web.setup.install;

import com.zrlog.business.service.TemplateInfoHelper;
import com.zrlog.common.Constants;
import com.zrlog.common.vo.TemplateVO;
import com.zrlog.theme.spi.BundledThemeProvider;
import com.zrlog.theme.spi.BundledThemes;
import org.junit.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/** Checks the application dependency assembly, including independently published resource JARs. */
public class BundledThemeAssemblyTest {
    @Test
    public void applicationIncludesDefaultAndOptionalThemesWithReadableMetadata() {
        BundledThemes themes = BundledThemes.getInstance();
        assertEquals(Set.of("default", "template-www", "hexo-theme-fluid", "hexo-theme-butterfly",
                        "hexo-theme-shiro", "hexo-theme-next"),
                themes.providers().stream().map(BundledThemeProvider::id).collect(Collectors.toSet()));
        assertEquals("/include/templates/default", Constants.getDefaultTemplatePath());
        for (BundledThemeProvider provider : themes.providers()) {
            TemplateVO info = TemplateInfoHelper.loadTemplateVO(provider.path());
            assertNotNull(provider.id(), info);
            assertTrue(provider.id(), info.isClasspathTemplate());
            assertNotNull(provider.id(), info.getVersion());
            assertNotEquals(provider.id(), ".xx", info.getViewType());
            assertNotNull(provider.id(), info.getConfig());
            assertFalse(provider.id(), info.getConfig().isEmpty());
        }
    }
}
