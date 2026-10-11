package com.zrlog.updater;

interface Deployment {
    final class Target {
        final String project;
        final String id;
        final String image;
        final String platform;
        final String configurationFingerprint;

        Target(String project, String id, String image, String platform, String configurationFingerprint) {
            this.project = project; this.id = id; this.image = image; this.platform = platform;
            this.configurationFingerprint = configurationFingerprint;
        }
    }

    Target inspect() throws Exception;
    void pull(Protocol.Job job) throws Exception;
    void replace(Target expected, Protocol.Job job) throws Exception;
    void verify(Protocol.Job job) throws Exception;
}
