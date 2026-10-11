package com.zrlog.updater;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermissions;

final class AtomicFiles {
    private AtomicFiles() { }

    static void write(Path file, String content) throws IOException {
        if (Files.isSymbolicLink(file)) { throw new IOException("Refusing a symlink configuration file"); }
        Path directory = file.toAbsolutePath().getParent();
        Path temporary = Files.createTempFile(directory, ".updater-", ".tmp",
                PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rw-------")));
        try {
            if (Files.exists(file, LinkOption.NOFOLLOW_LINKS)) {
                PosixFileAttributes attributes = Files.readAttributes(file, PosixFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
                Files.setPosixFilePermissions(temporary, attributes.permissions());
                Files.setOwner(temporary, attributes.owner());
                Files.getFileAttributeView(temporary, java.nio.file.attribute.PosixFileAttributeView.class)
                        .setGroup(attributes.group());
            }
            try (FileChannel channel = FileChannel.open(temporary, StandardOpenOption.WRITE)) {
                ByteBuffer bytes = StandardCharsets.UTF_8.encode(content);
                while (bytes.hasRemaining()) { channel.write(bytes); }
                channel.force(true);
            }
            Files.move(temporary, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            try (FileChannel channel = FileChannel.open(directory, StandardOpenOption.READ)) { channel.force(true); }
        } finally { Files.deleteIfExists(temporary); }
    }

    static String read(Path file, int maxBytes) throws IOException {
        if (Files.isSymbolicLink(file)) { throw new IOException("Refusing a symlink configuration file"); }
        try (var stream = Files.newInputStream(file)) {
            byte[] bytes = stream.readNBytes(maxBytes + 1);
            if (bytes.length > maxBytes) { throw new IOException("Configuration exceeds size limit"); }
            return new String(bytes, StandardCharsets.UTF_8);
        }
    }
}
