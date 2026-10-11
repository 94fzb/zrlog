package com.zrlog.updater;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow;

final class Metadata {
    interface Fetcher { String get(URI uri) throws Exception; }
    final URI base;
    final String filename;
    private final Fetcher fetcher;

    Metadata(Properties properties, Fetcher fetcher) {
        String configuredBase = properties.getProperty("mirrorWebSite", "https://dl.zrlog.com/");
        base = URI.create(configuredBase.endsWith("/") ? configuredBase : configuredBase + "/");
        if (!"https".equals(base.getScheme()) || base.getHost() == null || base.getUserInfo() != null
                || base.getQuery() != null || base.getFragment() != null || !base.normalize().equals(base)) {
            throw new IllegalArgumentException("mirrorWebSite must be an absolute HTTPS base URL");
        }
        filename = properties.getProperty("updateVersionJsonFilename", "last.docker-image.version.json");
        if (!filename.matches("[A-Za-z0-9][A-Za-z0-9._-]*\\.json")) {
            throw new IllegalArgumentException("Invalid updateVersionJsonFilename");
        }
        this.fetcher = fetcher;
    }

    static Metadata fromBuildProperties() throws IOException {
        Properties properties = new Properties();
        try (InputStream stream = Metadata.class.getResourceAsStream("/build.properties")) {
            if (stream == null) { throw new IOException("Missing build.properties"); }
            properties.load(stream);
        }
        HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NEVER).build();
        return new Metadata(properties, uri -> {
            HttpRequest request = HttpRequest.newBuilder(uri).timeout(Duration.ofSeconds(20))
                    .header("Accept", "application/json").GET().build();
            // BodyHandlers.ofByteArray is bounded by a subscriber: a server cannot stream an unlimited manifest.
            HttpResponse<byte[]> response = client.send(request, limitedBody(1024 * 1024));
            if (response.statusCode() != 200) { throw new IOException("Metadata HTTP " + response.statusCode()); }
            return new String(response.body(), StandardCharsets.UTF_8);
        });
    }

    static HttpResponse.BodyHandler<byte[]> limitedBody(int maxBytes) {
        return response -> new HttpResponse.BodySubscriber<>() {
            final HttpResponse.BodySubscriber<byte[]> delegate = HttpResponse.BodySubscribers.ofByteArray();
            Flow.Subscription subscription;
            long size;
            boolean failed;
            @Override public CompletionStage<byte[]> getBody() { return delegate.getBody(); }
            @Override public void onSubscribe(Flow.Subscription value) { subscription = value; delegate.onSubscribe(value); }
            @Override public void onNext(List<ByteBuffer> buffers) {
                if (failed) { return; }
                for (ByteBuffer buffer : buffers) { size += buffer.remaining(); }
                if (size > maxBytes) {
                    failed = true;
                    subscription.cancel();
                    delegate.onError(new IOException("Metadata exceeds size limit"));
                } else { delegate.onNext(buffers); }
            }
            @Override public void onError(Throwable error) { delegate.onError(error); }
            @Override public void onComplete() { if (!failed) { delegate.onComplete(); } }
        };
    }

    URI uri(String channel) { channel(channel); return base.resolve(channel + "/" + filename); }

    Protocol.Manifest fetch(String channel) {
        try {
            String document = fetcher.get(uri(channel));
            if (document.length() > 1024 * 1024) { throw new IllegalArgumentException("Manifest too large"); }
            Protocol.Manifest manifest = Json.read(document, Protocol.Manifest.class);
            validate(manifest, channel);
            return manifest;
        } catch (Exception error) {
            throw new ApiException(502, "metadata_unavailable", "Unable to read a valid manifest from the configured metadata source");
        }
    }

    static void channel(String channel) {
        if (!"release".equals(channel) && !"preview".equals(channel)) { throw ApiException.invalid("Invalid channel"); }
    }

    static String repository(String value) {
        if (value == null || value.length() > 255 || !value.matches("[a-z0-9][a-z0-9.:-]*(?:/[a-z0-9]+(?:[._-][a-z0-9]+)*)+")) {
            throw ApiException.invalid("Invalid image repository");
        }
        String first = value.substring(0, value.indexOf('/'));
        if (!first.contains(".") && !first.contains(":") && !first.equals("localhost")) { value = "docker.io/" + value; }
        if (value.startsWith("index.docker.io/")) { value = "docker.io/" + value.substring("index.docker.io/".length()); }
        return value;
    }

    static String repositoryOfImage(String reference) {
        int digest = reference.indexOf('@');
        if (digest >= 0) { return repository(reference.substring(0, digest)); }
        int colon = reference.lastIndexOf(':');
        return repository(colon > reference.lastIndexOf('/') ? reference.substring(0, colon) : reference);
    }

    static void validate(Protocol.Manifest manifest, String channel) {
        if (manifest.schemaVersion != 1 || !"docker-image".equals(manifest.packageType)
                || !channel.equals(manifest.channel) || manifest.version == null
                || !manifest.version.matches("[0-9]+\\.[0-9]+\\.[0-9]+(?:-[A-Za-z0-9.-]+)?")
                || manifest.sourceCommit == null || !manifest.sourceCommit.matches("[0-9a-f]{40}")
                || manifest.buildId == null || !manifest.buildId.matches("[A-Za-z0-9][A-Za-z0-9._-]{0,127}")
                || ("release".equals(channel) && (!manifest.buildId.matches("[0-9a-f]{7,40}")
                    || !manifest.sourceCommit.startsWith(manifest.buildId)))
                || manifest.images == null || manifest.images.isEmpty() || manifest.images.size() > 32) {
            throw ApiException.invalid("Invalid Docker manifest identity");
        }
        OffsetDateTime.parse(manifest.releaseDate, DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ssXXX").withResolverStyle(ResolverStyle.STRICT));
        Set<String> targets = new HashSet<>();
        for (Protocol.Image image : manifest.images) {
            image.repository = repository(image.repository);
            if (image.digest == null || !image.digest.matches("sha256:[0-9a-f]{64}") || image.tag == null
                    || !image.tag.matches("[A-Za-z0-9_][A-Za-z0-9_.-]{0,127}")
                    || image.platforms == null || image.platforms.isEmpty() || image.platforms.size() > 16) {
                throw ApiException.invalid("Invalid Docker image entry");
            }
            image.platforms = image.platforms.stream().map(Metadata::platform).toList();
            for (String platform : image.platforms) {
                if (!targets.add(image.repository + "/" + platform)) {
                    throw ApiException.invalid("Invalid or ambiguous Docker platform");
                }
            }
        }
    }

    static Protocol.Image select(Protocol.Manifest manifest, String repository, String platform) {
        String canonical = repository(repository);
        String canonicalPlatform = platform(platform);
        return manifest.images.stream().filter(image -> canonical.equals(image.repository)
                && image.platforms.contains(canonicalPlatform)).findFirst()
                .orElseThrow(() -> new ApiException(409, "image_unavailable", "The selected repository has no published image for this platform"));
    }

    static String platform(String value) {
        if (value == null || !value.matches("[a-z0-9]+/[a-z0-9]+(?:/[a-z0-9]+)?")) {
            throw ApiException.invalid("Invalid Docker platform");
        }
        return "linux/arm64/v8".equals(value) ? "linux/arm64" : value;
    }
}
