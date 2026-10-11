package com.zrlog.updater;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class ApiServerTest {
    @Rule public TemporaryFolder temporary = new TemporaryFolder();
    private static final String TOKEN = "test-token-" + "a".repeat(40);
    private final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();

    @Test public void httpSubmitReturnsAJobBeforeDockerFinishesAndResultsSurviveClientDisconnect() throws Exception {
        Fixtures.FakeDeployment docker = new Fixtures.FakeDeployment();
        docker.pullStarted = new CountDownLatch(1); docker.continuePull = new CountDownLatch(1);
        try (StateStore store = new StateStore(temporary.newFolder().toPath());
             UpgradeService service = new UpgradeService(store, Fixtures.metadata(), docker);
             ApiServer server = new ApiServer(new InetSocketAddress("127.0.0.1", 0), TOKEN, service)) {
            server.start();
            String request = Json.GSON.toJson(Fixtures.request("http-background-001"));
            try {
                HttpResponse<String> accepted = call(server, "POST", "/v1/upgrades", TOKEN, request);
                assertEquals(202, accepted.statusCode());
                assertEquals("http-background-001", Json.read(accepted.body(), Protocol.Job.class).id);
                assertTrue(docker.pullStarted.await(2, TimeUnit.SECONDS));
                HttpResponse<String> status = call(server, "GET", "/v1/upgrades/http-background-001", TOKEN, null);
                assertEquals("pulling", Json.read(status.body(), Protocol.Job.class).phase);
                assertEquals(202, call(server, "POST", "/v1/upgrades", TOKEN, request).statusCode());
            } finally { docker.continuePull.countDown(); }
            assertEquals("succeeded", Fixtures.awaitTerminal(service, "http-background-001").phase);
            HttpResponse<String> result = call(server, "GET", "/v1/upgrades/http-background-001", TOKEN, null);
            assertEquals(200, result.statusCode());
            assertEquals("succeeded", Json.read(result.body(), Protocol.Job.class).phase);
            assertFalse(result.body().contains(TOKEN));
            assertEquals(1, docker.replacements);
        }
    }

    @Test public void unauthorizedArbitraryAndOversizedRequestsCannotTriggerAnUpgrade() throws Exception {
        Fixtures.FakeDeployment docker = new Fixtures.FakeDeployment();
        try (StateStore store = new StateStore(temporary.newFolder().toPath());
             UpgradeService service = new UpgradeService(store, Fixtures.metadata(), docker);
             ApiServer server = new ApiServer(new InetSocketAddress("127.0.0.1", 0), TOKEN, service)) {
            server.start();
            assertEquals(200, call(server, "GET", "/health", null, null).statusCode());
            assertEquals(401, call(server, "GET", "/v1/capabilities", null, null).statusCode());
            assertEquals(401, call(server, "POST", "/v1/upgrades", "invalid-token", "{}").statusCode());
            assertEquals(400, call(server, "POST", "/v1/upgrades", TOKEN,
                    "{\"image\":\"attacker/image:latest\",\"command\":\"rm\"}").statusCode());
            assertEquals(400, call(server, "PUT", "/v1/settings", TOKEN,
                    "{\"metadataUrl\":\"https://attacker.test/\"}").statusCode());
            assertEquals(413, call(server, "POST", "/v1/upgrades", TOKEN, " ".repeat(4097)).statusCode());
            assertEquals(400, call(server, "POST", "/v1/upgrades", TOKEN, "{broken}").statusCode());
            assertEquals(400, call(server, "POST", "/v1/upgrades", TOKEN, "null").statusCode());
            assertEquals(0, docker.replacements);
            assertNull(docker.pulled);
        }
    }

    private HttpResponse<String> call(ApiServer server, String method, String path, String token, String body) throws Exception {
        var builder = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + server.port() + path))
                .timeout(Duration.ofSeconds(3)).header("Content-Type", "application/json");
        if (token != null) { builder.header("Authorization", "Bearer " + token); }
        return client.send(builder.method(method, body == null ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(body)).build(), HttpResponse.BodyHandlers.ofString());
    }
}
