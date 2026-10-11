package com.zrlog.updater;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.Instant;

final class StateStore implements AutoCloseable {
    private final Path file;
    private final FileChannel channel;
    private final FileLock lock;
    private Protocol.State state;

    StateStore(Path directory) throws IOException {
        Files.createDirectories(directory);
        Files.setPosixFilePermissions(directory, PosixFilePermissions.fromString("rwx------"));
        file = directory.resolve("state.json");
        channel = FileChannel.open(directory.resolve("updater.lock"), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        FileLock acquired;
        try { acquired = channel.tryLock(); }
        catch (RuntimeException | IOException error) { channel.close(); throw error; }
        if (acquired == null) { channel.close(); throw new IOException("Another updater owns this state directory"); }
        lock = acquired;
        try {
            state = Files.exists(file) ? Json.read(AtomicFiles.read(file, 4 * 1024 * 1024), Protocol.State.class) : new Protocol.State();
            if (state.schemaVersion != 1 || state.settings == null || state.jobs == null || state.jobs.size() > 1000) {
                throw new IOException("Unsupported updater state");
            }
            for (Protocol.Job job : state.jobs) {
                if (!job.terminal()) {
                    job.recoveryRequired = "replacing".equals(job.phase) || "checking".equals(job.phase);
                    job.phase = "interrupted";
                    job.errorCode = "updater_restarted";
                    job.message = "Updater restarted before the result was verified. Check the deployment before retrying.";
                    job.updatedAt = Instant.now().toString();
                }
            }
            save(state);
        } catch (RuntimeException | IOException error) { close(); throw error; }
    }

    synchronized Protocol.State snapshot() { return Json.copy(state, Protocol.State.class); }

    synchronized void save(Protocol.State next) throws IOException {
        AtomicFiles.write(file, Json.GSON.toJson(next));
        state = Json.copy(next, Protocol.State.class);
    }

    @Override public void close() throws IOException { lock.release(); channel.close(); }
}
