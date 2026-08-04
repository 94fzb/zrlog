package com.zrlog.web;

import com.zrlog.util.BlogBuildInfoUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class ApplicationCommandLine {

    enum Command {
        SERVE, UPGRADE
    }

    private final Command command;
    private final String[] serveArgs;
    private final boolean preview;

    private ApplicationCommandLine(Command command, String[] serveArgs, boolean preview) {
        this.command = command;
        this.serveArgs = serveArgs;
        this.preview = preview;
    }

    static ApplicationCommandLine parse(String[] args) {
        List<String> values = new ArrayList<>(args == null ? List.of() : Arrays.asList(args));
        if (values.contains("--upgrade")) {
            throw new IllegalArgumentException("Use the 'upgrade' command instead of '--upgrade'");
        }
        Command command = Command.SERVE;
        if (!values.isEmpty() && "upgrade".equals(values.get(0))) {
            command = Command.UPGRADE;
            values.remove(0);
        } else if (!values.isEmpty() && "serve".equals(values.get(0))) {
            values.remove(0);
        }
        boolean preview = !BlogBuildInfoUtil.isRelease();
        List<String> serveArgs = new ArrayList<>();
        for (String value : values) {
            if (command == Command.UPGRADE && value.startsWith("--channel=")) {
                String channel = value.substring("--channel=".length());
                if (!"release".equals(channel) && !"preview".equals(channel)) {
                    throw new IllegalArgumentException("Unsupported upgrade channel: " + channel);
                }
                preview = "preview".equals(channel);
                continue;
            }
            serveArgs.add(value);
        }
        return new ApplicationCommandLine(command, serveArgs.toArray(new String[0]), preview);
    }

    Command getCommand() {
        return command;
    }

    String[] getServeArgs() {
        return serveArgs.clone();
    }

    boolean isPreview() {
        return preview;
    }
}
