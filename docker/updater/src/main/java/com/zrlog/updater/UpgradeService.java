package com.zrlog.updater;

import java.io.IOException;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

final class UpgradeService implements AutoCloseable {
    private final StateStore store;
    private final Metadata metadata;
    private final Deployment deployment;
    private final ExecutorService worker = Executors.newSingleThreadExecutor();
    private boolean storageFailed;
    private boolean closing;

    UpgradeService(StateStore store, Metadata metadata, Deployment deployment) {
        this.store = store; this.metadata = metadata; this.deployment = deployment;
    }

    synchronized Protocol.Capabilities capabilities() throws Exception {
        available();
        Deployment.Target target = deployment.inspect();
        Protocol.Capabilities response = new Protocol.Capabilities();
        response.ready = true;
        response.metadataBaseUrl = metadata.base.toString();
        response.manifestFilename = metadata.filename;
        response.project = target.project;
        response.platform = target.platform;
        response.currentImage = target.image;
        response.repository = selectedRepository(store.snapshot().settings, target);
        return response;
    }

    synchronized Protocol.SettingsResponse settings(String channel) throws Exception {
        available();
        Metadata.channel(channel);
        Deployment.Target target = deployment.inspect();
        Protocol.SettingsResponse response = new Protocol.SettingsResponse();
        response.settings = store.snapshot().settings;
        response.currentRepository = Metadata.repositoryOfImage(target.image);
        response.images = metadata.fetch(channel).images.stream().filter(image -> image.platforms.contains(target.platform)).toList();
        return response;
    }

    synchronized Protocol.Settings saveSettings(Protocol.Settings settings) throws Exception {
        available();
        Metadata.channel(settings.channel);
        if (settings.repository == null) { throw ApiException.invalid("repository is required"); }
        Deployment.Target target = deployment.inspect();
        settings.repository = settings.repository.isBlank() ? "" : Metadata.repository(settings.repository);
        Metadata.select(metadata.fetch(settings.channel), selectedRepository(settings, target), target.platform);
        Protocol.State next = store.snapshot();
        next.settings = Json.copy(settings, Protocol.Settings.class);
        save(next);
        return Json.copy(next.settings, Protocol.Settings.class);
    }

    synchronized Protocol.Job submit(Protocol.UpgradeRequest request) throws Exception {
        available();
        if (request.requestId == null || !request.requestId.matches("[a-zA-Z0-9][a-zA-Z0-9_-]{15,79}")) {
            throw ApiException.invalid("requestId must be a unique 16–80 character identifier");
        }
        Metadata.channel(request.channel);
        Protocol.State state = store.snapshot();
        for (Protocol.Job previous : state.jobs) {
            if (request.requestId.equals(previous.id)) {
                if (!Objects.equals(request.version, previous.version) || !Objects.equals(request.buildId, previous.buildId)
                        || !request.channel.equals(previous.channel)) {
                    throw new ApiException(409, "request_id_conflict", "This requestId already belongs to another target");
                }
                return previous;
            }
        }
        if (state.jobs.stream().anyMatch(job -> !job.terminal())) {
            throw new ApiException(409, "upgrade_in_progress", "An upgrade is already running");
        }
        if (state.jobs.size() >= 1000) {
            throw new ApiException(409, "history_full", "Archive the updater state before submitting more upgrades");
        }
        Protocol.Manifest manifest = metadata.fetch(request.channel);
        if (!Objects.equals(manifest.version, request.version) || !Objects.equals(manifest.buildId, request.buildId)) {
            throw new ApiException(409, "version_changed", "The available version changed; check for updates again");
        }
        Deployment.Target target = deployment.inspect();
        Protocol.Image image = Metadata.select(manifest, selectedRepository(state.settings, target), target.platform);
        Protocol.Job job = new Protocol.Job();
        job.id = request.requestId;
        job.channel = request.channel;
        job.version = manifest.version;
        job.buildId = manifest.buildId;
        job.sourceCommit = manifest.sourceCommit;
        job.repository = image.repository;
        job.digest = image.digest;
        job.platform = target.platform;
        job.previousImage = target.image;
        job.metadataUrl = metadata.uri(request.channel).toString();
        job.phase = "queued";
        job.message = "Upgrade queued";
        job.createdAt = job.updatedAt = Instant.now().toString();
        state.jobs.add(job);
        save(state); // No engine mutation before durable acknowledgement.
        Protocol.Job accepted = Json.copy(job, Protocol.Job.class);
        worker.execute(() -> execute(target, job));
        return accepted;
    }

    synchronized Protocol.Job status(String id) {
        return store.snapshot().jobs.stream().filter(job -> job.id.equals(id)).findFirst()
                .orElseThrow(() -> new ApiException(404, "task_not_found", "Upgrade task not found"));
    }

    private void execute(Deployment.Target target, Protocol.Job job) {
        try {
            phase(job, "pulling", "Downloading and verifying the selected image");
            deployment.pull(job);
            phase(job, "replacing", "Replacing the zrlog container");
            job.recoveryRequired = true;
            deployment.replace(target, job);
            phase(job, "checking", "Verifying the running application");
            deployment.verify(job);
            job.recoveryRequired = false;
            phase(job, "succeeded", "The target version is running and healthy");
        } catch (Exception error) {
            job.errorCode = error instanceof ApiException api ? api.code : "upgrade_failed";
            String message = error instanceof ApiException ? error.getMessage() : "Upgrade failed; inspect the deployment before retrying";
            try { phase(job, "failed", message); }
            catch (IOException storageError) {
                synchronized (this) { storageFailed = true; }
                System.err.println("Unable to persist the upgrade result; refusing new tasks");
            }
        }
    }

    private synchronized void phase(Protocol.Job job, String phase, String message) throws IOException {
        job.phase = phase;
        job.message = message;
        job.updatedAt = Instant.now().toString();
        Protocol.State next = store.snapshot();
        for (int index = 0; index < next.jobs.size(); index++) {
            if (next.jobs.get(index).id.equals(job.id)) {
                next.jobs.set(index, Json.copy(job, Protocol.Job.class));
                save(next);
                return;
            }
        }
        throw new IOException("Upgrade state missing");
    }

    private void save(Protocol.State state) throws IOException {
        try { store.save(state); }
        catch (IOException error) { storageFailed = true; throw error; }
    }

    private void available() {
        if (storageFailed || closing) { throw new ApiException(503, "updater_unavailable", "Updater is not accepting tasks"); }
    }

    private static String selectedRepository(Protocol.Settings settings, Deployment.Target target) {
        return settings.repository == null || settings.repository.isBlank()
                ? Metadata.repositoryOfImage(target.image) : Metadata.repository(settings.repository);
    }

    @Override public void close() {
        synchronized (this) { closing = true; }
        worker.shutdown();
        try {
            if (!worker.awaitTermination(30, TimeUnit.SECONDS)) { worker.shutdownNow(); worker.awaitTermination(5, TimeUnit.SECONDS); }
        } catch (InterruptedException error) { worker.shutdownNow(); Thread.currentThread().interrupt(); }
    }
}
