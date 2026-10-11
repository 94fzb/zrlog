package com.zrlog.updater;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Operates only the configured Compose project's zrlog service; no HTTP-controlled paths or commands. */
final class DockerDeployment implements Deployment {
    private static final Pattern ENV_ASSIGNMENT = Pattern.compile(
            "^[ \\t]*(?:export[ \\t]+)?([A-Za-z_][A-Za-z0-9_.-]*)[ \\t]*(?:=[ \\t]*|:[ \\t]+)(.*)");
    interface CommandRunner { String run(List<String> command, Path directory, Duration timeout) throws Exception; }
    private final Path directory;
    private final List<Path> files;
    private final String project;
    private final CommandRunner commands;

    DockerDeployment(Path directory, List<String> filenames, String project, CommandRunner commands) throws IOException {
        this.directory = directory.toRealPath();
        if (!directory.isAbsolute() || !this.directory.equals(directory.normalize())
                || !project.matches("[a-z0-9][a-z0-9_-]{0,127}") || filenames.isEmpty()) {
            throw new IllegalArgumentException("An absolute deployment directory and existing Compose project name are required");
        }
        this.project = project;
        this.commands = commands;
        List<Path> resolved = new ArrayList<>();
        for (String filename : filenames) {
            Path file = this.directory.resolve(filename).normalize();
            if (!file.startsWith(this.directory) || !file.toRealPath().equals(file) || !Files.isRegularFile(file)) {
                throw new IllegalArgumentException("Compose files must be regular files under the deployment directory");
            }
            resolved.add(file);
        }
        files = List.copyOf(resolved);
    }

    @Override public Target inspect() throws Exception {
        Path env = directory.resolve(".env");
        if (!Files.isRegularFile(env) || !Files.isWritable(env) || !Files.isWritable(directory) || Files.isSymbolicLink(env)) {
            throw new ApiException(409, "configuration_not_writable", "The deployment .env and its directory must be writable regular paths");
        }
        JsonObject config = Json.tree(compose(Duration.ofSeconds(20), "config", "--format", "json")).getAsJsonObject();
        JsonObject service = config.getAsJsonObject("services").getAsJsonObject("zrlog");
        if (service == null || service.has("build") || service.has("scale") && service.get("scale").getAsInt() != 1) {
            throw new ApiException(409, "unsupported_deployment", "A single zrlog service using a published image is required");
        }
        if (service.has("ports")) {
            for (var port : service.getAsJsonArray("ports")) {
                String published = string(port.getAsJsonObject(), "published");
                if (!published.matches("[1-9][0-9]{0,4}") || Integer.parseInt(published) > 65535) {
                    throw new ApiException(409, "unsupported_deployment", "Published ports must use a fixed host port to preserve the site's address");
                }
            }
        }
        String id = compose(Duration.ofSeconds(20), "ps", "--all", "--quiet", "zrlog").trim();
        if (!id.matches("[a-f0-9]{12,64}")) {
            throw new ApiException(409, "target_not_found", "Exactly one existing zrlog container is required");
        }
        JsonObject container = container(id);
        JsonObject labels = container.getAsJsonObject("Config").getAsJsonObject("Labels");
        String hashLine = compose(Duration.ofSeconds(20), "config", "--hash", "zrlog").trim();
        String hash = hashLine.substring(hashLine.lastIndexOf(' ') + 1);
        if (!project.equals(string(labels, "com.docker.compose.project"))
                || !"zrlog".equals(string(labels, "com.docker.compose.service"))
                || !directory.toString().equals(string(labels, "com.docker.compose.project.working_dir"))
                || !hash.matches("[a-f0-9]{64}") || !hash.equals(string(labels, "com.docker.compose.config-hash"))
                || !String.join(",", files.stream().map(Path::toString).toList())
                    .equals(string(labels, "com.docker.compose.project.config_files"))) {
            throw new ApiException(409, "deployment_drift", "Compose configuration differs from the running deployment; reconcile it before upgrading");
        }
        if (!container.getAsJsonObject("State").get("Running").getAsBoolean()) {
            throw new ApiException(409, "target_not_running", "The current zrlog container is not running");
        }
        // The running image's platform is authoritative (the daemon may run it under emulation).
        JsonObject image = inspectImage(string(container, "Image"));
        String platform = string(image, "Os") + "/" + string(image, "Architecture");
        if (image.has("Variant") && !string(image, "Variant").isBlank()) { platform += "/" + string(image, "Variant"); }
        platform = Metadata.platform(platform);
        if (!platform.startsWith("linux/")) {
            throw new ApiException(409, "unsupported_platform", "The Compose updater currently requires Linux containers");
        }
        String currentImage = string(container.getAsJsonObject("Config"), "Image");
        if (!Objects.equals(currentImage, string(service, "image"))) {
            throw new ApiException(409, "deployment_drift", "Compose image differs from the running image");
        }
        return new Target(project, id, currentImage, platform, fingerprint(config.toString()));
    }

    @Override public void pull(Protocol.Job job) throws Exception {
        docker(Duration.ofMinutes(15), "pull", "--quiet", "--platform", job.platform, job.reference());
        JsonObject image = inspectImage(job.reference());
        JsonObject labels = image.getAsJsonObject("Config").getAsJsonObject("Labels");
        if (!job.version.equals(string(labels, "org.opencontainers.image.version"))
                || !job.sourceCommit.equals(string(labels, "org.opencontainers.image.revision"))) {
            throw new ApiException(409, "image_identity_mismatch", "Pulled image does not match the manifest's version and source commit");
        }
        String platform = string(image, "Os") + "/" + string(image, "Architecture");
        if (image.has("Variant") && !string(image, "Variant").isBlank()) { platform += "/" + string(image, "Variant"); }
        platform = Metadata.platform(platform);
        if (!job.platform.equals(platform)) {
            throw new ApiException(409, "image_platform_mismatch", "Pulled image does not match the selected platform");
        }
    }

    @Override public void replace(Target expected, Protocol.Job job) throws Exception {
        Target current = inspect();
        if (!current.id.equals(expected.id) || !current.configurationFingerprint.equals(expected.configurationFingerprint)) {
            throw new ApiException(409, "deployment_changed", "Deployment changed while downloading the image; refresh before retrying");
        }
        Path env = directory.resolve(".env");
        String original = AtomicFiles.read(env, 1024 * 1024);
        String updated = replaceImage(original, job.reference());
        JsonObject before = Json.tree(compose(Duration.ofSeconds(20), "config", "--format", "json")).getAsJsonObject();
        if (!current.configurationFingerprint.equals(fingerprint(before.toString()))) {
            throw new ApiException(409, "deployment_changed", "Deployment changed while preparing the upgrade");
        }
        // Verify that writing the .env key will actually change this service (reject hard-coded YAML images).
        Path probe = Files.createTempFile(directory, ".updater-env-", ".tmp");
        try {
            AtomicFiles.write(probe, updated);
            JsonObject config = Json.tree(composeWithEnv(probe, Duration.ofSeconds(20), "config", "--format", "json")).getAsJsonObject();
            if (!job.reference().equals(string(config.getAsJsonObject("services").getAsJsonObject("zrlog"), "image"))) {
                throw new ApiException(409, "image_not_configurable", "The zrlog service image must use ZRLOG_IMAGE from .env");
            }
            config.getAsJsonObject("services").getAsJsonObject("zrlog").addProperty("image", current.image);
            if (!before.equals(config)) {
                throw new ApiException(409, "configuration_changed", "Changing ZRLOG_IMAGE must not change other Compose settings");
            }
            if (!original.equals(AtomicFiles.read(env, 1024 * 1024))) {
                throw new ApiException(409, "deployment_changed", "Deployment .env changed while preparing the upgrade");
            }
            AtomicFiles.write(env, updated);
        } finally { Files.deleteIfExists(probe); }
        // Compose owns preservation of ports, networks, volumes, environment, labels and restart policy.
        // Pin is persisted before replacement. A crash cannot make a later compose up silently revert it.
        compose(Duration.ofMinutes(4), "up", "--detach", "--no-deps", "--no-build", "--pull", "never",
                "--wait", "--wait-timeout", "150", "zrlog");
    }

    @Override public void verify(Protocol.Job job) throws Exception {
        Target target = inspect();
        JsonObject container = container(target.id);
        JsonObject health = container.getAsJsonObject("State").getAsJsonObject("Health");
        if (!job.reference().equals(target.image) || health == null || !"healthy".equals(string(health, "Status"))
                || !string(inspectImage(job.reference()), "Id").equals(string(container, "Image"))) {
            throw new ApiException(502, "upgrade_not_verified", "The target image is not running and healthy");
        }
        String output = docker(Duration.ofSeconds(20), "exec", target.id, "/opt/zrlog/zrlog", "--version");
        Pattern identity = Pattern.compile("(?m)^zrlog version:[\\t ]*"
                + Pattern.quote(job.version + " - " + job.buildId) + " \\(");
        if (!identity.matcher(output).find()) {
            throw new ApiException(502, "running_version_mismatch", "The running application does not report the target build");
        }
    }

    static String replaceImage(String contents, String reference) {
        StringBuilder output = new StringBuilder();
        boolean replaced = false;
        int offset = 0;
        while (offset < contents.length()) {
            int end = lineEnd(contents, offset);
            Matcher assignment = ENV_ASSIGNMENT.matcher(contents.substring(offset, end));
            if (assignment.find()) {
                int value = offset + assignment.start(2);
                // Compose permits quoted values across lines, including lines resembling assignments.
                if (value < contents.length() && (contents.charAt(value) == '\'' || contents.charAt(value) == '"')) {
                    end = lineEnd(contents, quotedValueEnd(contents, value));
                }
                if ("ZRLOG_IMAGE".equals(assignment.group(1))) {
                    output.append("ZRLOG_IMAGE=").append(reference);
                    if (end > offset && contents.charAt(end - 1) == '\n') {
                        output.append(end > 1 && contents.charAt(end - 2) == '\r' ? "\r\n" : "\n");
                    }
                    replaced = true;
                } else { output.append(contents, offset, end); }
            } else { output.append(contents, offset, end); }
            offset = end;
        }
        if (!replaced) {
            if (!contents.isEmpty() && !contents.endsWith("\n")) { output.append('\n'); }
            output.append("ZRLOG_IMAGE=").append(reference).append('\n');
        }
        return output.toString();
    }

    private static int lineEnd(String text, int start) {
        int newline = text.indexOf('\n', start);
        return newline < 0 ? text.length() : newline + 1;
    }

    private static int quotedValueEnd(String text, int start) {
        char quote = text.charAt(start);
        for (int index = start + 1; index < text.length(); index++) {
            char character = text.charAt(index);
            if (character == '\\' && index + 1 < text.length()
                    && (text.charAt(index + 1) == quote || text.charAt(index + 1) == '\\')) { index++; }
            else if (character == quote) { return index + 1; }
        }
        throw new ApiException(409, "invalid_configuration", "The deployment .env contains an unterminated quoted value");
    }

    private String fingerprint(String resolved) throws Exception {
        MessageDigest hash = MessageDigest.getInstance("SHA-256");
        for (Path file : files) { hash.update(AtomicFiles.read(file, 1024 * 1024).getBytes(StandardCharsets.UTF_8)); hash.update((byte) 0); }
        hash.update(AtomicFiles.read(directory.resolve(".env"), 1024 * 1024).getBytes(StandardCharsets.UTF_8));
        hash.update(resolved.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(hash.digest());
    }

    private JsonObject container(String id) throws Exception {
        JsonArray values = Json.tree(docker(Duration.ofSeconds(20), "container", "inspect", id)).getAsJsonArray();
        if (values.size() != 1) { throw new IOException("Expected one container"); }
        return values.get(0).getAsJsonObject();
    }

    private JsonObject inspectImage(String reference) throws Exception {
        JsonArray values = Json.tree(docker(Duration.ofSeconds(20), "image", "inspect", reference)).getAsJsonArray();
        if (values.size() != 1) { throw new IOException("Expected one image"); }
        return values.get(0).getAsJsonObject();
    }

    private String compose(Duration timeout, String... arguments) throws Exception {
        return composeWithEnv(directory.resolve(".env"), timeout, arguments);
    }

    private String composeWithEnv(Path env, Duration timeout, String... arguments) throws Exception {
        List<String> command = new ArrayList<>(List.of("docker", "compose", "--project-name", project,
                "--project-directory", directory.toString(), "--env-file", env.toString()));
        for (Path file : files) { command.add("--file"); command.add(file.toString()); }
        command.addAll(List.of(arguments));
        return commands.run(command, directory, timeout);
    }

    private String docker(Duration timeout, String... arguments) throws Exception {
        List<String> command = new ArrayList<>(List.of("docker"));
        command.addAll(List.of(arguments));
        return commands.run(command, directory, timeout);
    }

    private static String string(JsonObject object, String key) {
        return object == null || !object.has(key) || object.get(key).isJsonNull() ? "" : object.get(key).getAsString();
    }
}
