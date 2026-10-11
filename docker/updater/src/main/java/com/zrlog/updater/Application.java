package com.zrlog.updater;

import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.Arrays;

public final class Application {
    private Application() { }

    public static void main(String[] args) throws Exception {
        if (args.length == 1 && "--healthcheck".equals(args[0])) {
            var request = java.net.http.HttpRequest.newBuilder(java.net.URI.create("http://127.0.0.1:"
                    + env("UPDATER_PORT", "8081") + "/health")).timeout(java.time.Duration.ofSeconds(3)).GET().build();
            var response = java.net.http.HttpClient.newHttpClient().send(request, java.net.http.HttpResponse.BodyHandlers.discarding());
            if (response.statusCode() != 200) { throw new IllegalStateException("Updater health check failed"); }
            return;
        }
        if (args.length == 1 && "--version".equals(args[0])) {
            var properties = new java.util.Properties();
            try (var input = Application.class.getResourceAsStream("/build.properties")) { properties.load(input); }
            System.out.println("zrlog-updater " + properties.getProperty("version"));
            return;
        }
        if (args.length != 0) { throw new IllegalArgumentException("Unsupported arguments"); }
        System.setProperty("sun.net.httpserver.maxReqTime", "30");
        System.setProperty("sun.net.httpserver.maxRspTime", "30");
        System.setProperty("sun.net.httpserver.maxReqHeaders", "64");
        String token = required("UPDATER_TOKEN");
        String project = required("UPDATER_PROJECT");
        Path directory = Path.of(required("UPDATER_DEPLOYMENT_DIR"));
        var filenames = Arrays.asList(env("UPDATER_COMPOSE_FILES", "docker-compose.yml,docker-compose.updater.yml").split(","));
        Metadata metadata = Metadata.fromBuildProperties();
        DockerDeployment deployment = new DockerDeployment(directory, filenames, project, new Commands());
        StateStore store = new StateStore(Path.of(env("UPDATER_STATE_DIR", "/var/lib/zrlog-updater")));
        UpgradeService upgrades = new UpgradeService(store, metadata, deployment);
        ApiServer server;
        try {
            server = new ApiServer(new InetSocketAddress(env("UPDATER_BIND", "0.0.0.0"),
                    Integer.parseInt(env("UPDATER_PORT", "8081"))), token, upgrades);
        } catch (Exception error) { upgrades.close(); store.close(); throw error; }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.close();
            upgrades.close();
            try { store.close(); } catch (Exception error) { System.err.println("Unable to close updater state"); }
        }));
        server.start();
        System.out.println("ZrLog updater HTTP service started on port " + server.port());
    }

    private static String env(String key, String fallback) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? fallback : value;
    }

    private static String required(String key) {
        String value = env(key, "");
        if (value.isBlank()) { throw new IllegalArgumentException(key + " is required"); }
        return value;
    }
}
