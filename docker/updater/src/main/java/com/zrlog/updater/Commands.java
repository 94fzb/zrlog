package com.zrlog.updater;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/** Executes argument vectors, never shell text. Output may contain deployment secrets and is never logged. */
final class Commands implements DockerDeployment.CommandRunner {
    @Override public String run(List<String> command, Path directory, Duration timeout) throws Exception {
        ProcessBuilder builder = new ProcessBuilder(command).directory(directory.toFile());
        builder.environment().keySet().removeIf(key -> key.startsWith("COMPOSE_") || key.startsWith("DOCKER_") || key.equals("ZRLOG_IMAGE"));
        builder.environment().put("DOCKER_HOST", "unix:///var/run/docker.sock");
        builder.environment().put("COMPOSE_ANSI", "never");
        // Compose warnings must not contaminate JSON stdout. Stderr can contain interpolated secrets.
        builder.redirectError(ProcessBuilder.Redirect.DISCARD);
        Process process = builder.start();
        process.getOutputStream().close();
        CompletableFuture<byte[]> output = CompletableFuture.supplyAsync(() -> {
            try (InputStream input = process.getInputStream(); ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
                byte[] bytes = new byte[8192];
                int count;
                while ((count = input.read(bytes)) != -1) {
                    if (buffer.size() + count > 4 * 1024 * 1024) {
                        process.destroyForcibly();
                        throw new IOException("Docker command output exceeded limit");
                    }
                    buffer.write(bytes, 0, count);
                }
                return buffer.toByteArray();
            } catch (IOException error) { throw new java.io.UncheckedIOException(error); }
        });
        try {
            if (!process.waitFor(timeout.toMillis(), TimeUnit.MILLISECONDS)) {
                throw new ApiException(504, "docker_timeout", "Docker command timed out; inspect the deployment before retrying");
            }
            byte[] bytes = output.get(5, TimeUnit.SECONDS);
            if (process.exitValue() != 0) {
                throw new ApiException(502, "docker_command_failed", "Docker operation failed (exit " + process.exitValue() + ")");
            }
            return new String(bytes, StandardCharsets.UTF_8).trim();
        } finally {
            if (process.isAlive()) {
                // Docker CLI starts the Compose plugin as a child process.
                process.descendants().toList().forEach(ProcessHandle::destroyForcibly);
                process.destroyForcibly();
            }
            process.getInputStream().close();
        }
    }
}
