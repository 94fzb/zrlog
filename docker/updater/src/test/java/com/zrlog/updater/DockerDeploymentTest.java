package com.zrlog.updater;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class DockerDeploymentTest {
    @Rule public TemporaryFolder temporary = new TemporaryFolder();

    @Test public void replacementPinsComposeConfigurationAndLeavesOtherSettingsUntouched() throws Exception {
        Path root = deploymentFiles();
        Engine engine = new Engine(root);
        DockerDeployment deployment = new DockerDeployment(root, List.of("docker-compose.yml"), "existing-blog", engine);
        Deployment.Target old = deployment.inspect();
        Protocol.Job job = job();
        deployment.pull(job);
        deployment.replace(old, job);
        deployment.verify(job);
        assertTrue(engine.running);
        assertEquals(job.reference(), engine.runningImage);
        assertEquals("MYSQL_PASSWORD=test-secret\nZRLOG_IMAGE=" + job.reference() + "\nZRLOG_PORT=9090\n", Files.readString(root.resolve(".env")));
        assertEquals("unchanged compose mounts and networks\n", Files.readString(root.resolve("docker-compose.yml")));
        assertEquals(1, engine.upCommands.size());
        List<String> command = engine.upCommands.get(0);
        assertTrue(command.containsAll(List.of("--no-deps", "--no-build", "--pull", "never", "--wait")));
        assertEquals("zrlog", command.get(command.size() - 1));
        assertFalse(engine.calls.stream().anyMatch(call -> call.contains("down") || call.contains("prune")));
    }

    @Test public void changedComposeOrEnvironmentDuringDownloadStopsBeforeReplacement() throws Exception {
        Path root = deploymentFiles();
        Engine engine = new Engine(root);
        DockerDeployment deployment = new DockerDeployment(root, List.of("docker-compose.yml"), "existing-blog", engine);
        Deployment.Target old = deployment.inspect();
        deployment.pull(job());
        Files.writeString(root.resolve("docker-compose.yml"), "operator changed configuration\n");
        assertEquals("deployment_changed", assertThrows(ApiException.class, () -> deployment.replace(old, job())).code);
        assertTrue(engine.running);
        assertTrue(engine.upCommands.isEmpty());
        assertTrue(Files.readString(root.resolve(".env")).contains("ZRLOG_IMAGE=" + Fixtures.ALIYUN + ":4.0.0"));
    }

    @Test public void incompatibleImageAndDriftAreRejectedBeforeAnyServiceIsRecreated() throws Exception {
        Path root = deploymentFiles();
        Engine engine = new Engine(root);
        DockerDeployment deployment = new DockerDeployment(root, List.of("docker-compose.yml"), "existing-blog", engine);
        engine.wrongImageVersion = true;
        assertEquals("image_identity_mismatch", assertThrows(ApiException.class, () -> deployment.pull(job())).code);
        engine.wrongImageVersion = false;
        engine.drift = true;
        assertEquals("deployment_drift", assertThrows(ApiException.class, deployment::inspect).code);
        assertTrue(engine.upCommands.isEmpty());
    }

    @Test public void hardcodedImageIsRejectedWithoutOverwritingTheEnvFile() throws Exception {
        Path root = deploymentFiles();
        Engine engine = new Engine(root); engine.hardcodedImage = true;
        DockerDeployment deployment = new DockerDeployment(root, List.of("docker-compose.yml"), "existing-blog", engine);
        Deployment.Target old = deployment.inspect();
        String before = Files.readString(root.resolve(".env"));
        assertEquals("image_not_configurable", assertThrows(ApiException.class, () -> deployment.replace(old, job())).code);
        assertEquals(before, Files.readString(root.resolve(".env")));
        assertTrue(engine.upCommands.isEmpty());
    }

    @Test public void dynamicHostPortsAreRejectedBeforeReplacingTheSite() throws Exception {
        Path root = deploymentFiles();
        Engine engine = new Engine(root);
        DockerDeployment deployment = new DockerDeployment(root, List.of("docker-compose.yml"), "existing-blog", engine);
        for (String published : List.of("", "0", "8000-9000", "65536")) {
            engine.publishedPort = published;
            assertEquals("unsupported_deployment", assertThrows(ApiException.class, deployment::inspect).code);
        }
        assertTrue(engine.upCommands.isEmpty());
        engine.publishedPort = "9090";
        assertNotNull(deployment.inspect());
    }

    @Test public void imagePinPreservesQuotedMultilineValuesCommentsAndLineEndings() {
        String before = "# deployment\r\nPASSWORD='first line\\'s quote\r\nZRLOG_IMAGE=inside-password\r\nlast line'\r\n"
                + "export ZRLOG_IMAGE=old\r\nTOKEN=\"first\\\"line\nZRLOG_IMAGE=inside-token\nlast\"\nPORT=9090";
        String expected = before.replace("export ZRLOG_IMAGE=old", "ZRLOG_IMAGE=" + job().reference());
        assertEquals(expected, DockerDeployment.replaceImage(before, job().reference()));
        String withoutImage = "PASSWORD='first\nZRLOG_IMAGE=inside-password\nlast'";
        assertEquals(withoutImage + "\nZRLOG_IMAGE=" + job().reference() + "\n",
                DockerDeployment.replaceImage(withoutImage, job().reference()));
        assertThrows(ApiException.class, () -> DockerDeployment.replaceImage("PASSWORD='unfinished", job().reference()));
    }

    @Test public void pinCannotChangeOtherServicesOrTheirConfiguration() throws Exception {
        Path root = deploymentFiles();
        Engine engine = new Engine(root); engine.sharedImageVariable = true;
        DockerDeployment deployment = new DockerDeployment(root, List.of("docker-compose.yml"), "existing-blog", engine);
        Deployment.Target original = deployment.inspect();
        String before = Files.readString(root.resolve(".env"));
        assertEquals("configuration_changed", assertThrows(ApiException.class, () -> deployment.replace(original, job())).code);
        assertEquals(before, Files.readString(root.resolve(".env")));
        assertTrue(engine.running);
        assertTrue(engine.upCommands.isEmpty());
    }

    @Test public void noHealthOrIncorrectReportedVersionCannotProduceSuccess() throws Exception {
        Path root = deploymentFiles();
        Engine engine = new Engine(root);
        DockerDeployment deployment = new DockerDeployment(root, List.of("docker-compose.yml"), "existing-blog", engine);
        deployment.replace(deployment.inspect(), job());
        engine.healthy = false;
        assertEquals("upgrade_not_verified", assertThrows(ApiException.class, () -> deployment.verify(job())).code);
        engine.healthy = true; engine.wrongRuntimeVersion = true;
        assertEquals("running_version_mismatch", assertThrows(ApiException.class, () -> deployment.verify(job())).code);
    }

    private Path deploymentFiles() throws Exception {
        Path root = temporary.newFolder().toPath().toRealPath();
        Files.writeString(root.resolve("docker-compose.yml"), "unchanged compose mounts and networks\n");
        Files.writeString(root.resolve(".env"), "MYSQL_PASSWORD=test-secret\nZRLOG_IMAGE=" + Fixtures.ALIYUN + ":4.0.0\nZRLOG_PORT=9090\n");
        return root;
    }

    private Protocol.Job job() {
        Protocol.Job job = new Protocol.Job();
        job.repository = Fixtures.ALIYUN; job.digest = Fixtures.DIGEST; job.platform = "linux/amd64";
        job.version = "4.0.1"; job.buildId = "abcdef1"; job.sourceCommit = Fixtures.COMMIT;
        return job;
    }

    /** A Docker/Compose boundary fixture: source files are real, engine state is isolated. */
    private static final class Engine implements DockerDeployment.CommandRunner {
        final Path root;
        final List<List<String>> calls = new ArrayList<>();
        final List<List<String>> upCommands = new ArrayList<>();
        String runningImage = Fixtures.ALIYUN + ":4.0.0";
        String publishedPort = "9090";
        boolean running = true, healthy = true, hardcodedImage, drift, wrongImageVersion, wrongRuntimeVersion, sharedImageVariable;
        Engine(Path root) { this.root = root; }

        @Override public String run(List<String> command, Path directory, Duration timeout) throws Exception {
            calls.add(List.copyOf(command));
            if (command.get(1).equals("compose")) {
                Path env = Path.of(command.get(command.indexOf("--env-file") + 1));
                String image = Files.readAllLines(env).stream().filter(line -> line.startsWith("ZRLOG_IMAGE="))
                        .map(line -> line.substring("ZRLOG_IMAGE=".length())).findFirst().orElseThrow();
                if (command.contains("--hash")) { return "zrlog " + "a".repeat(64); }
                if (command.contains("config")) {
                    JsonObject target = new JsonObject(); target.addProperty("image", hardcodedImage ? runningImage : image);
                    JsonObject port = new JsonObject(); port.addProperty("published", publishedPort);
                    JsonArray ports = new JsonArray(); ports.add(port); target.add("ports", ports);
                    JsonObject services = new JsonObject(); services.add("zrlog", target);
                    if (sharedImageVariable) {
                        JsonObject otherService = new JsonObject(); otherService.addProperty("image", image);
                        services.add("another-service", otherService);
                    }
                    JsonObject config = new JsonObject(); config.add("services", services);
                    return config.toString();
                }
                if (command.contains("ps")) { return "b".repeat(64); }
                if (command.contains("up")) { runningImage = image; upCommands.add(command); return ""; }
            }
            if (command.get(1).equals("pull")) { return ""; }
            if (command.get(1).equals("exec")) {
                return wrongRuntimeVersion ? "zrlog version: 4.0.0 - 1111111 (2026-10-01)" : "zrlog version: 4.0.1 - abcdef1 (2026-10-11)";
            }
            if (command.get(1).equals("image")) {
                JsonObject labels = new JsonObject();
                labels.addProperty("org.opencontainers.image.version", wrongImageVersion ? "4.0.0" : "4.0.1");
                labels.addProperty("org.opencontainers.image.revision", Fixtures.COMMIT);
                JsonObject config = new JsonObject(); config.add("Labels", labels);
                JsonObject image = new JsonObject(); image.add("Config", config); image.addProperty("Id", "sha256:" + "c".repeat(64));
                image.addProperty("Os", "linux"); image.addProperty("Architecture", "amd64");
                return array(image);
            }
            if (command.get(1).equals("container")) {
                JsonObject labels = new JsonObject();
                labels.addProperty("com.docker.compose.project", "existing-blog");
                labels.addProperty("com.docker.compose.service", "zrlog");
                labels.addProperty("com.docker.compose.project.working_dir", root.toString());
                labels.addProperty("com.docker.compose.project.config_files", root.resolve("docker-compose.yml").toString());
                labels.addProperty("com.docker.compose.config-hash", (drift ? "0" : "a").repeat(64));
                JsonObject config = new JsonObject(); config.add("Labels", labels); config.addProperty("Image", runningImage);
                JsonObject health = new JsonObject(); health.addProperty("Status", healthy ? "healthy" : "unhealthy");
                JsonObject state = new JsonObject(); state.addProperty("Running", running); state.add("Health", health);
                JsonObject container = new JsonObject(); container.add("Config", config); container.add("State", state);
                container.addProperty("Image", "sha256:" + "c".repeat(64));
                return array(container);
            }
            throw new AssertionError("Unexpected command " + command);
        }

        private String array(JsonObject value) { JsonArray values = new JsonArray(); values.add(value); return values.toString(); }
    }
}
