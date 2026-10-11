package com.zrlog.updater;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.*;

public class MetadataTest {
    @Test public void configuredBuildPropertiesSelectMetadataWithoutConsultingGithubOrAnotherOrigin() {
        Properties properties = new Properties();
        properties.setProperty("mirrorWebSite", "https://updates.example.test/zrlog");
        properties.setProperty("updateVersionJsonFilename", "custom-docker.version.json");
        List<String> requested = new ArrayList<>();
        Metadata metadata = new Metadata(properties, uri -> {
            requested.add(uri.toString());
            return Json.GSON.toJson(Fixtures.manifest());
        });
        Protocol.Image image = Metadata.select(metadata.fetch("release"), Fixtures.ALIYUN, "linux/arm64");
        assertEquals(Fixtures.ALIYUN + "@" + Fixtures.DIGEST, image.reference());
        assertEquals(List.of("https://updates.example.test/zrlog/release/custom-docker.version.json"), requested);
    }

    @Test public void eachRepositoryUsesItsOwnDigestAndMissingPlatformsNeverFallBack() {
        Protocol.Manifest manifest = Fixtures.manifest();
        Metadata.validate(manifest, "release");
        assertEquals(Fixtures.HUB_DIGEST, Metadata.select(manifest, "zrlog/zrlog", "linux/amd64").digest);
        assertEquals(Fixtures.DIGEST, Metadata.select(manifest, Fixtures.ALIYUN, "linux/amd64").digest);
        assertEquals(Fixtures.DIGEST, Metadata.select(manifest, Fixtures.ALIYUN, "linux/arm64/v8").digest);
        assertEquals("image_unavailable", assertThrows(ApiException.class,
                () -> Metadata.select(manifest, Fixtures.ALIYUN, "windows/amd64")).code);
        assertThrows(ApiException.class, () -> Metadata.select(manifest, "registry.example.test/unpublished/zrlog", "linux/amd64"));
    }

    @Test public void malformedIdentityAndDuplicateRepositoryPlatformAreRejected() {
        Protocol.Manifest manifest = Fixtures.manifest();
        manifest.sourceCommit = "9".repeat(40);
        assertThrows(ApiException.class, () -> Metadata.validate(manifest, "release"));
        manifest.sourceCommit = Fixtures.COMMIT;
        manifest.images = List.of(manifest.images.get(0), manifest.images.get(0));
        assertThrows(ApiException.class, () -> Metadata.validate(manifest, "release"));
        manifest.images = Fixtures.manifest().images;
        manifest.images.get(0).digest = "latest";
        assertThrows(ApiException.class, () -> Metadata.validate(manifest, "release"));
    }

    @Test public void unavailableMetadataStopsAtTheConfiguredSource() {
        List<String> requested = new ArrayList<>();
        Metadata metadata = new Metadata(new Properties(), uri -> { requested.add(uri.toString()); throw new java.io.IOException("offline"); });
        assertEquals("metadata_unavailable", assertThrows(ApiException.class, () -> metadata.fetch("release")).code);
        assertEquals(List.of("https://dl.zrlog.com/release/last.docker-image.version.json"), requested);
    }

    @Test public void endpointConfigurationRejectsNonHttpsCredentialsQueriesAndFilenameTraversal() {
        for (String origin : List.of("http://example.test/", "https://user:secret@example.test/", "https://example.test/?url=evil", "https://example.test/a/../")) {
            Properties properties = new Properties(); properties.setProperty("mirrorWebSite", origin);
            assertThrows(IllegalArgumentException.class, () -> new Metadata(properties, uri -> ""));
        }
        Properties properties = new Properties(); properties.setProperty("updateVersionJsonFilename", "../other.json");
        assertThrows(IllegalArgumentException.class, () -> new Metadata(properties, uri -> ""));
    }
}
