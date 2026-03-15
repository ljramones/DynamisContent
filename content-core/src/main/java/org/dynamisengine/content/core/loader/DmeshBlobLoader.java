package org.dynamisengine.content.core.loader;

import org.dynamisengine.core.exception.DynamisException;
import org.dynamisengine.content.api.id.AssetId;
import org.dynamisengine.content.api.id.AssetType;
import org.dynamisengine.content.api.id.AssetTypes;
import org.dynamisengine.content.api.loader.AssetLoader;
import org.dynamisengine.content.api.loader.AssetResolver;
import org.dynamisengine.content.api.manifest.DmeshBlob;
import org.dynamisengine.content.api.manifest.ManifestEntry;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;

public final class DmeshBlobLoader implements AssetLoader<DmeshBlob> {

    private static final byte[] MAGIC = new byte[]{'D', 'M', 'E', 'S', 'H', 0, 0, 0};
    private static final int HEADER_BYTES = MAGIC.length + Integer.BYTES + Integer.BYTES + Integer.BYTES;

    private final Path baseDir;

    public DmeshBlobLoader(Path baseDir) {
        this.baseDir = Objects.requireNonNull(baseDir, "baseDir");
    }

    @Override
    public AssetType<DmeshBlob> type() {
        return AssetTypes.DMESH_BLOB;
    }

    @Override
    public DmeshBlob load(AssetId id, ManifestEntry entry, AssetResolver resolver) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(entry, "entry");

        Path dmeshPath = resolvePath(entry.uri());
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(dmeshPath);
        } catch (IOException e) {
            throw new DynamisException("Failed to read .dmesh: " + dmeshPath, e);
        }

        if (bytes.length < HEADER_BYTES) {
            throw new DynamisException("Invalid .dmesh (too short): " + dmeshPath);
        }

        try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes))) {
            byte[] magic = in.readNBytes(MAGIC.length);
            if (!Arrays.equals(magic, MAGIC)) {
                throw new DynamisException("Invalid .dmesh magic for " + dmeshPath);
            }

            int version = in.readInt();
            int flags = in.readInt();
            int payloadLength = in.readInt();
            if (payloadLength < 0) {
                throw new DynamisException("Invalid .dmesh payload length for " + dmeshPath + ": " + payloadLength);
            }

            byte[] payload = in.readNBytes(payloadLength);
            if (payload.length != payloadLength) {
                throw new DynamisException("Unexpected EOF in .dmesh payload for " + dmeshPath);
            }

            if (flags != 0) {
                throw new DynamisException("Unsupported .dmesh flags for " + dmeshPath + ": " + flags);
            }

            return new DmeshBlob(version, payload, truncatedSha256ToLong(bytes));
        } catch (IOException e) {
            throw new DynamisException("Failed to parse .dmesh: " + dmeshPath, e);
        }
    }

    private Path resolvePath(String uri) {
        Path uriPath = Path.of(uri);
        if (uriPath.isAbsolute()) {
            return uriPath;
        }
        return baseDir.resolve(uriPath).normalize();
    }

    private static long truncatedSha256ToLong(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            long value = 0L;
            for (int i = 0; i < Long.BYTES; i++) {
                value = (value << 8) | (hash[i] & 0xffL);
            }
            return value;
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }
}
