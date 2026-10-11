package com.zrlog.updater;

import java.util.ArrayList;
import java.util.List;

/** Fixed HTTP/storage structures. No application, installation or database dependencies. */
final class Protocol {
    private Protocol() { }

    static final class UpgradeRequest {
        String requestId;
        String channel;
        String version;
        String buildId;
    }

    static final class Settings {
        String repository = "";
        String channel = "release";
    }

    static final class SettingsResponse {
        Settings settings;
        String currentRepository;
        List<Image> images;
    }

    static final class Manifest {
        int schemaVersion;
        String packageType;
        String channel;
        String version;
        String buildId;
        String sourceCommit;
        String releaseDate;
        List<Image> images;
    }

    static final class Image {
        String repository;
        String tag;
        String digest;
        List<String> platforms;

        String reference() { return repository + "@" + digest; }
    }

    static final class Job {
        String id;
        String channel;
        String version;
        String buildId;
        String sourceCommit;
        String repository;
        String digest;
        String platform;
        String metadataUrl;
        String previousImage;
        String phase;
        String errorCode;
        String message;
        String createdAt;
        String updatedAt;
        boolean recoveryRequired;

        boolean terminal() {
            return "succeeded".equals(phase) || "failed".equals(phase) || "interrupted".equals(phase);
        }

        String reference() { return repository + "@" + digest; }
    }

    static final class State {
        int schemaVersion = 1;
        Settings settings = new Settings();
        List<Job> jobs = new ArrayList<>();
    }

    static final class Capabilities {
        int protocolVersion = 1;
        boolean ready;
        String metadataBaseUrl;
        String manifestFilename;
        String project;
        String platform;
        String currentImage;
        String repository;
    }

    static final class ErrorResponse {
        final String code;
        final String message;
        ErrorResponse(String code, String message) { this.code = code; this.message = message; }
    }

    static final class Health {
        final String status = "ok";
    }
}
