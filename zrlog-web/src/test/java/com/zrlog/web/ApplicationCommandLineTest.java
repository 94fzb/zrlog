package com.zrlog.web;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class ApplicationCommandLineTest {

    @Test
    public void shouldParseUpgradeAndRemoveOneShotArgumentsFromServeArgs() {
        ApplicationCommandLine commandLine = ApplicationCommandLine.parse(new String[]{
                "upgrade", "--channel=preview", "--port=19080", "--contextPath=/blog"
        });

        assertEquals(ApplicationCommandLine.Command.UPGRADE, commandLine.getCommand());
        assertTrue(commandLine.isPreview());
        assertArrayEquals(new String[]{"--port=19080", "--contextPath=/blog"}, commandLine.getServeArgs());
    }

    @Test
    public void shouldSupportExplicitServeCommand() {
        ApplicationCommandLine commandLine = ApplicationCommandLine.parse(new String[]{"serve", "--port=19081"});

        assertEquals(ApplicationCommandLine.Command.SERVE, commandLine.getCommand());
        assertArrayEquals(new String[]{"--port=19081"}, commandLine.getServeArgs());
    }

    @Test
    public void shouldUseReleaseChannelWhenRequested() {
        ApplicationCommandLine commandLine = ApplicationCommandLine.parse(new String[]{
                "upgrade", "--channel=release"
        });

        assertFalse(commandLine.isPreview());
        assertArrayEquals(new String[0], commandLine.getServeArgs());
    }

    @Test
    public void shouldRejectUpgradeOptionAndUnknownChannel() {
        assertThrows(IllegalArgumentException.class,
                () -> ApplicationCommandLine.parse(new String[]{"--upgrade"}));
        assertThrows(IllegalArgumentException.class,
                () -> ApplicationCommandLine.parse(new String[]{"upgrade", "--channel=nightly"}));
    }
}
