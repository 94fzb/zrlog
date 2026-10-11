package com.zrlog.updater;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

final class ApiServer implements AutoCloseable {
    private final HttpServer server;
    private final UpgradeService upgrades;
    private final byte[] authorization;
    private final ThreadPoolExecutor executor;

    ApiServer(InetSocketAddress address, String token, UpgradeService upgrades) throws IOException {
        if (token == null || token.length() < 32 || token.length() > 256 || !token.matches("[A-Za-z0-9_-]+")) {
            throw new IllegalArgumentException("UPDATER_TOKEN must contain 32–256 URL-safe characters");
        }
        this.upgrades = upgrades;
        authorization = ("Bearer " + token).getBytes(StandardCharsets.UTF_8);
        server = HttpServer.create(address, 16);
        executor = new ThreadPoolExecutor(4, 4, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(32));
        server.setExecutor(executor);
        server.createContext("/", this::handle);
    }

    void start() { server.start(); }
    int port() { return server.getAddress().getPort(); }

    private void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getRawPath();
            String method = exchange.getRequestMethod();
            if ("/health".equals(path) && "GET".equals(method)) { send(exchange, 200, new Protocol.Health()); return; }
            String provided = exchange.getRequestHeaders().getFirst("Authorization");
            if (provided == null || !MessageDigest.isEqual(authorization, provided.getBytes(StandardCharsets.UTF_8))) {
                throw new ApiException(401, "unauthorized", "A valid updater token is required");
            }
            if (exchange.getRequestURI().getRawQuery() != null) { throw ApiException.invalid("Query parameters are not supported"); }
            if ("/v1/capabilities".equals(path) && "GET".equals(method)) { send(exchange, 200, upgrades.capabilities()); }
            else if ("/v1/settings".equals(path) && "GET".equals(method)) { send(exchange, 200, upgrades.settings("release")); }
            else if ("/v1/settings/preview".equals(path) && "GET".equals(method)) { send(exchange, 200, upgrades.settings("preview")); }
            else if ("/v1/settings".equals(path) && "PUT".equals(method)) {
                send(exchange, 200, upgrades.saveSettings(body(exchange, Protocol.Settings.class, Set.of("repository", "channel"))));
            } else if ("/v1/upgrades".equals(path) && "POST".equals(method)) {
                Protocol.Job job = upgrades.submit(body(exchange, Protocol.UpgradeRequest.class,
                        Set.of("requestId", "channel", "version", "buildId")));
                send(exchange, job.terminal() ? 200 : 202, job);
            } else if (path.matches("/v1/upgrades/[A-Za-z0-9_-]{16,80}") && "GET".equals(method)) {
                send(exchange, 200, upgrades.status(path.substring("/v1/upgrades/".length())));
            } else { throw new ApiException(404, "not_found", "Unknown updater endpoint"); }
        } catch (ApiException error) { send(exchange, error.status, new Protocol.ErrorResponse(error.code, error.getMessage())); }
        catch (Exception error) { send(exchange, 503, new Protocol.ErrorResponse("updater_unavailable", "Updater could not complete this request")); }
        finally { exchange.close(); }
    }

    private <T> T body(HttpExchange exchange, Class<T> type, Set<String> fields) throws IOException {
        String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
        if (contentType == null || !contentType.split(";", 2)[0].strip().equalsIgnoreCase("application/json")) {
            throw new ApiException(415, "unsupported_media_type", "Content-Type must be application/json");
        }
        byte[] bytes = exchange.getRequestBody().readNBytes(4097);
        if (bytes.length > 4096) { throw new ApiException(413, "request_too_large", "Request exceeds 4096 bytes"); }
        String text = new String(bytes, StandardCharsets.UTF_8);
        JsonObject tree = Json.read(text, JsonObject.class);
        if (!fields.containsAll(tree.keySet())) { throw ApiException.invalid("Unknown request fields"); }
        return Json.read(text, type);
    }

    private void send(HttpExchange exchange, int status, Object value) throws IOException {
        byte[] bytes = Json.GSON.toJson(value).getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.getResponseHeaders().set("Cache-Control", "no-store");
        exchange.getResponseHeaders().set("X-Content-Type-Options", "nosniff");
        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
    }

    @Override public void close() { server.stop(1); executor.shutdownNow(); }
}
