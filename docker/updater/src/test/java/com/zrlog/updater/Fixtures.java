package com.zrlog.updater;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

final class Fixtures {
    static final String ALIYUN = "registry.cn-chengdu.aliyuncs.com/hibegin/zrlog";
    static final String HUB = "docker.io/zrlog/zrlog";
    static final String COMMIT = "abcdef1234567890abcdef1234567890abcdef12";
    static final String DIGEST = "sha256:" + "1".repeat(64);
    static final String HUB_DIGEST = "sha256:" + "2".repeat(64);

    static Protocol.Manifest manifest() {
        Protocol.Manifest manifest = new Protocol.Manifest();
        manifest.schemaVersion = 1;
        manifest.packageType = "docker-image";
        manifest.channel = "release";
        manifest.version = "4.0.1";
        manifest.buildId = "abcdef1";
        manifest.sourceCommit = COMMIT;
        manifest.releaseDate = "2026-10-11 13:00:00Z";
        Protocol.Image aliyun = new Protocol.Image();
        aliyun.repository = ALIYUN; aliyun.tag = "4.0.1"; aliyun.digest = DIGEST;
        aliyun.platforms = List.of("linux/amd64", "linux/arm64");
        Protocol.Image hub = new Protocol.Image();
        hub.repository = HUB; hub.tag = "4.0.1"; hub.digest = HUB_DIGEST; hub.platforms = aliyun.platforms;
        manifest.images = List.of(aliyun, hub);
        return manifest;
    }

    static Metadata metadata() {
        return new Metadata(new Properties(), uri -> Json.GSON.toJson(manifest()));
    }

    static Protocol.UpgradeRequest request(String id) {
        Protocol.UpgradeRequest request = new Protocol.UpgradeRequest();
        request.requestId = id; request.channel = "release"; request.version = "4.0.1"; request.buildId = "abcdef1";
        return request;
    }

    static Protocol.Job awaitTerminal(UpgradeService service, String id) throws Exception {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
        while (System.nanoTime() < deadline) {
            Protocol.Job job = service.status(id);
            if (job.terminal()) { return job; }
            Thread.sleep(10);
        }
        throw new AssertionError("Upgrade did not reach a terminal state");
    }

    static class FakeDeployment implements Deployment {
        String image = ALIYUN + ":4.0.0";
        volatile boolean running = true;
        volatile String pulled;
        volatile String composeImage = image;
        volatile String persistedAttachment = "existing-user-photo";
        volatile int replacements;
        boolean failPull;
        boolean failVerify;
        CountDownLatch pullStarted;
        CountDownLatch continuePull;

        @Override public Target inspect() { return new Target("existing-blog", "a".repeat(64), image, "linux/amd64", "original-config"); }
        @Override public void pull(Protocol.Job job) throws Exception {
            if (pullStarted != null) { pullStarted.countDown(); }
            if (continuePull != null && !continuePull.await(5, TimeUnit.SECONDS)) { throw new IllegalStateException("Test pull timeout"); }
            if (failPull) { throw new ApiException(502, "docker_command_failed", "Unable to pull image"); }
            pulled = job.reference();
        }
        @Override public void replace(Target expected, Protocol.Job job) {
            replacements++;
            composeImage = image = job.reference();
            running = true;
        }
        @Override public void verify(Protocol.Job job) {
            if (failVerify) { throw new ApiException(502, "upgrade_not_verified", "Container did not become healthy"); }
            if (!image.equals(job.reference())) { throw new AssertionError("Wrong image"); }
        }
    }
}
