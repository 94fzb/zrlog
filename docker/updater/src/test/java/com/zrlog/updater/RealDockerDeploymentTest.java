package com.zrlog.updater;

import org.junit.Test;
import org.junit.Assume;

import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

/** Isolated real-engine contract test, opt-in locally and enabled by the updater CI job. */
public class RealDockerDeploymentTest {
    @Test
    public void preservesVolumesPortsAndComposePinWhenReplacingAnImage() throws Exception {
        Assume.assumeTrue("Set ZRLOG_UPDATER_REAL_DOCKER=1 for the isolated engine test",
                "1".equals(System.getenv("ZRLOG_UPDATER_REAL_DOCKER")));
        Path dir = Files.createTempDirectory("zrlog-updater-engine-").toRealPath();
        String project = "updater-test-" + UUID.randomUUID().toString().substring(0, 8);
        String registry = project + "-registry";
        Commands commands = new Commands();
        List<String> createdImages = new ArrayList<>();
        try {
            run(commands, dir, "docker", "run", "--detach", "--name", registry,
                    "--publish", "127.0.0.1::5000", "registry:2");
            String address = run(commands, dir, "docker", "port", registry, "5000/tcp").trim();
            String repository = "localhost:" + address.substring(address.lastIndexOf(':') + 1) + "/zrlog";
            String commit = "abcdef1" + "0".repeat(33);
            Files.writeString(dir.resolve("Dockerfile"), """
                    FROM busybox:1.37.0
                    ARG VERSION
                    ARG COMMIT
                    LABEL org.opencontainers.image.version=$VERSION
                    LABEL org.opencontainers.image.revision=$COMMIT
                    RUN mkdir -p /opt/zrlog /www && echo ok > /www/index.html && \
                        printf '#!/bin/sh\\necho "zrlog version: %s - abcdef1 (2026-10-11)"\\n' "$VERSION" > /opt/zrlog/zrlog && \
                        chmod +x /opt/zrlog/zrlog
                    CMD ["httpd", "-f", "-p", "8080", "-h", "/www"]
                    """);
            for (String version : List.of("99.0.0", "99.0.1")) {
                createdImages.add(repository + ":" + version);
                run(commands, dir, "docker", "build", "--build-arg", "VERSION=" + version,
                        "--build-arg", "COMMIT=" + commit, "--tag", repository + ":" + version, ".");
                run(commands, dir, "docker", "push", repository + ":" + version);
            }
            String digest = Json.tree(run(commands, dir, "docker", "image", "inspect", repository + ":99.0.1"))
                    .getAsJsonArray().get(0).getAsJsonObject().getAsJsonArray("RepoDigests").get(0).getAsString().split("@")[1];
            int port;
            try (ServerSocket socket = new ServerSocket(0)) { port = socket.getLocalPort(); }
            Files.writeString(dir.resolve(".env"), "ZRLOG_IMAGE=" + repository + ":99.0.0\nPRESERVED=yes\n");
            Files.writeString(dir.resolve("docker-compose.yml"), """
                    services:
                      zrlog:
                        image: ${ZRLOG_IMAGE}
                        restart: unless-stopped
                        environment:
                          SENTINEL: original
                        ports: ["127.0.0.1:%d:8080"]
                        volumes: ["attachments:/attachments"]
                        healthcheck:
                          test: ["CMD", "wget", "-q", "--spider", "http://127.0.0.1:8080"]
                          interval: 1s
                          timeout: 2s
                          retries: 20
                    volumes:
                      attachments:
                    """.formatted(port));
            compose(commands, dir, project, "up", "--detach", "--wait");
            DockerDeployment deployment = new DockerDeployment(dir, List.of("docker-compose.yml"), project, commands);
            Deployment.Target original = deployment.inspect();
            run(commands, dir, "docker", "exec", original.id, "sh", "-c", "echo attachment-data > /attachments/sentinel");
            Protocol.Job job = new Protocol.Job();
            job.version = "99.0.1"; job.buildId = "abcdef1"; job.sourceCommit = commit;
            job.repository = repository; job.platform = original.platform; job.digest = "sha256:" + "b".repeat(64);
            assertThrows(Exception.class, () -> deployment.pull(job));
            assertEquals(original.id, deployment.inspect().id);
            job.digest = digest;
            deployment.pull(job);
            deployment.replace(original, job);
            deployment.verify(job);
            Deployment.Target replaced = deployment.inspect();
            assertNotEquals(original.id, replaced.id);
            assertEquals("attachment-data", run(commands, dir, "docker", "exec", replaced.id, "cat", "/attachments/sentinel").trim());
            assertEquals("original", run(commands, dir, "docker", "exec", replaced.id, "printenv", "SENTINEL").trim());
            assertEquals("127.0.0.1:" + port, run(commands, dir, "docker", "port", replaced.id, "8080/tcp").trim());
            assertTrue(Files.readString(dir.resolve(".env")).contains("PRESERVED=yes"));
            assertTrue(Files.readString(dir.resolve(".env")).contains("ZRLOG_IMAGE=" + job.reference()));
            compose(commands, dir, project, "up", "--detach", "--wait");
            assertEquals(replaced.id, deployment.inspect().id);
        } finally {
            try { compose(commands, dir, project, "down", "--volumes"); } catch (Exception ignored) { }
            try { run(commands, dir, "docker", "rm", "--force", "--volumes", registry); } catch (Exception ignored) { }
            for (String image : createdImages) {
                try { run(commands, dir, "docker", "image", "rm", "--force", image); } catch (Exception ignored) { }
            }
            try (var paths = Files.walk(dir)) {
                for (Path path : paths.sorted(java.util.Comparator.reverseOrder()).toList()) { Files.deleteIfExists(path); }
            }
        }
    }

    private static String compose(Commands commands, Path dir, String project, String... args) throws Exception {
        List<String> command = new ArrayList<>(List.of("docker", "compose", "--project-directory", dir.toString(),
                "--project-name", project, "--env-file", dir.resolve(".env").toString(), "--file", dir.resolve("docker-compose.yml").toString()));
        command.addAll(Arrays.asList(args));
        return commands.run(command, dir, Duration.ofMinutes(3));
    }

    private static String run(Commands commands, Path dir, String... command) throws Exception {
        return commands.run(List.of(command), dir, Duration.ofMinutes(3));
    }
}
