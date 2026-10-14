package com.zrlog.web;
import com.zrlog.common.UpdaterTypeEnum;
import com.zrlog.common.updater.UpdaterHosts;
import com.zrlog.common.vo.Version;
import com.zrlog.updater.spi.*;
import org.junit.Test;
import java.io.File;
import static org.junit.Assert.*;
public class ApplicationUpgradeRunnerTest {
    @Test public void debCommandDelegatesBeforeVersionPluginAndPreservesExitCode() {
        FakeDeb deb = new FakeDeb();
        UpdaterHosts.DelegatingUpdater host = new UpdaterHosts.DelegatingUpdater(new UpdateContext.Builder().build(), deb);
        assertEquals(7, ApplicationUpgradeRunner.run(host, true, true));
        assertEquals("preview", deb.channel); assertTrue(deb.json);
    }
    private static class FakeDeb implements SystemPackageUpdater {
        String channel; boolean json;
        public int install(String channel, boolean json) { this.channel=channel; this.json=json; return 7; }
        public boolean available() { return true; }
        public void request(String channel) { throw new AssertionError("CLI must install rather than enqueue"); }
        public DebUpgradeStatus status() { return null; }
        public void restartProcessAsync(Version version) { throw new AssertionError(); }
        public File getUpdateTempPath() { return null; }
        public String getUnzipPath() { return null; }
        public File execFile() { return null; }
        public UpdaterTypeEnum getType() { return UpdaterTypeEnum.DEB; }
    }
}
