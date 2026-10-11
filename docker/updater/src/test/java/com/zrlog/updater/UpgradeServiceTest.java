package com.zrlog.updater;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class UpgradeServiceTest {
    @Rule public TemporaryFolder temporary = new TemporaryFolder();

    @Test public void upgradingFromAliyunPinsItsDigestAndPersistsTheResultAcrossRestart() throws Exception {
        Path path = temporary.newFolder().toPath();
        Fixtures.FakeDeployment docker = new Fixtures.FakeDeployment();
        String id = "upgrade-success-0001";
        try (StateStore store = new StateStore(path); UpgradeService service = new UpgradeService(store, Fixtures.metadata(), docker)) {
            Protocol.Job accepted = service.submit(Fixtures.request(id));
            assertEquals("queued", accepted.phase);
            Protocol.Job job = Fixtures.awaitTerminal(service, id);
            assertEquals("succeeded", job.phase);
            assertEquals(Fixtures.ALIYUN + "@" + Fixtures.DIGEST, docker.pulled);
            assertEquals(docker.pulled, docker.composeImage);
            assertFalse(job.recoveryRequired);
        }
        try (StateStore store = new StateStore(path); UpgradeService service = new UpgradeService(store, Fixtures.metadata(), docker)) {
            assertEquals("succeeded", service.status(id).phase);
            assertEquals("succeeded", service.submit(Fixtures.request(id)).phase);
            assertEquals(1, docker.replacements);
        }
    }

    @Test public void failedPullPreservesTheRunningContainerAndComposeImage() throws Exception {
        Fixtures.FakeDeployment docker = new Fixtures.FakeDeployment(); docker.failPull = true;
        try (StateStore store = new StateStore(temporary.newFolder().toPath()); UpgradeService service = new UpgradeService(store, Fixtures.metadata(), docker)) {
            String oldImage = docker.image;
            service.submit(Fixtures.request("upgrade-pull-fail-01"));
            Protocol.Job job = Fixtures.awaitTerminal(service, "upgrade-pull-fail-01");
            assertEquals("failed", job.phase);
            assertEquals("docker_command_failed", job.errorCode);
            assertFalse(job.recoveryRequired);
            assertTrue(docker.running);
            assertEquals(oldImage, docker.composeImage);
            assertEquals(0, docker.replacements);
        }
    }

    @Test public void concurrentRequestsAreRejectedAndDuplicateRequestReturnsTheSameJob() throws Exception {
        Fixtures.FakeDeployment docker = new Fixtures.FakeDeployment();
        docker.pullStarted = new CountDownLatch(1); docker.continuePull = new CountDownLatch(1);
        try (StateStore store = new StateStore(temporary.newFolder().toPath()); UpgradeService service = new UpgradeService(store, Fixtures.metadata(), docker)) {
            try {
                service.submit(Fixtures.request("upgrade-concurrent-1"));
                assertTrue(docker.pullStarted.await(2, TimeUnit.SECONDS));
                assertEquals("upgrade-concurrent-1", service.submit(Fixtures.request("upgrade-concurrent-1")).id);
                assertEquals("upgrade_in_progress", assertThrows(ApiException.class,
                        () -> service.submit(Fixtures.request("upgrade-concurrent-2"))).code);
                Protocol.UpgradeRequest changed = Fixtures.request("upgrade-concurrent-1"); changed.version = "4.0.2";
                assertEquals("request_id_conflict", assertThrows(ApiException.class, () -> service.submit(changed)).code);
            } finally { docker.continuePull.countDown(); }
            assertEquals("succeeded", Fixtures.awaitTerminal(service, "upgrade-concurrent-1").phase);
        }
    }

    @Test public void savingARepositoryChangesFutureTasksWithoutTriggeringAnUpgrade() throws Exception {
        Fixtures.FakeDeployment docker = new Fixtures.FakeDeployment();
        try (StateStore store = new StateStore(temporary.newFolder().toPath()); UpgradeService service = new UpgradeService(store, Fixtures.metadata(), docker)) {
            Protocol.Settings settings = new Protocol.Settings(); settings.repository = "zrlog/zrlog";
            service.saveSettings(settings);
            assertNull(docker.pulled);
            assertEquals(0, docker.replacements);
            assertEquals(Fixtures.HUB, service.capabilities().repository);
            settings.repository = "unknown.test/private/zrlog";
            assertThrows(ApiException.class, () -> service.saveSettings(settings));
            assertEquals(Fixtures.HUB, service.capabilities().repository);
            service.submit(Fixtures.request("upgrade-selected-001"));
            assertEquals("succeeded", Fixtures.awaitTerminal(service, "upgrade-selected-001").phase);
            assertEquals(Fixtures.HUB + "@" + Fixtures.HUB_DIGEST, docker.pulled);
        }
    }

    @Test public void aChangedManifestAndFailedReadinessAreNotReportedAsSuccess() throws Exception {
        Fixtures.FakeDeployment docker = new Fixtures.FakeDeployment(); docker.failVerify = true;
        try (StateStore store = new StateStore(temporary.newFolder().toPath()); UpgradeService service = new UpgradeService(store, Fixtures.metadata(), docker)) {
            Protocol.UpgradeRequest stale = Fixtures.request("upgrade-stale-00001"); stale.buildId = "1234567";
            assertEquals("version_changed", assertThrows(ApiException.class, () -> service.submit(stale)).code);
            assertNull(docker.pulled);
            service.submit(Fixtures.request("upgrade-unhealthy-01"));
            Protocol.Job job = Fixtures.awaitTerminal(service, "upgrade-unhealthy-01");
            assertEquals("failed", job.phase);
            assertTrue(job.recoveryRequired);
            assertEquals("upgrade_not_verified", job.errorCode);
        }
    }

    @Test public void unfinishedTasksAreMarkedInterruptedWithoutRepeatingDockerOperations() throws Exception {
        Path path = temporary.newFolder().toPath();
        try (StateStore store = new StateStore(path)) {
            Protocol.State state = store.snapshot();
            Protocol.Job job = new Protocol.Job(); job.id = "interrupted-job-001"; job.phase = "replacing";
            state.jobs.add(job); store.save(state);
        }
        Fixtures.FakeDeployment docker = new Fixtures.FakeDeployment();
        try (StateStore store = new StateStore(path); UpgradeService service = new UpgradeService(store, Fixtures.metadata(), docker)) {
            Protocol.Job job = service.status("interrupted-job-001");
            assertEquals("interrupted", job.phase);
            assertTrue(job.recoveryRequired);
            assertEquals("updater_restarted", job.errorCode);
            assertEquals(0, docker.replacements);
        }
    }
}
