package org.dynamiscontent.core.loader;

import org.dynamis.core.exception.DynamisException;
import org.dynamiscontent.api.id.AssetId;
import org.dynamiscontent.api.loader.AssetResolver;
import org.dynamiscontent.api.manifest.DmeshBlob;
import org.dynamiscontent.api.manifest.ManifestEntry;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DmeshBlobLoaderTest {

    @Test
    void loadsValidDmeshBlob() throws Exception {
        Path baseDir = Files.createTempDirectory("dmesh-loader");
        Path file = baseDir.resolve("cooked/mesh/props/crate.dmesh");
        Files.createDirectories(file.getParent());

        byte[] payload = new byte[]{'a', 'b', 'c'};
        byte[] fullFile = createDmeshFile(1, 0, payload);
        Files.write(file, fullFile);

        DmeshBlobLoader loader = new DmeshBlobLoader(baseDir);
        ManifestEntry entry = new ManifestEntry(
                AssetId.of("mesh/props/crate"),
                "mesh.packed.dmesh.v0",
                "cooked/mesh/props/crate.dmesh",
                List.of()
        );

        DmeshBlob blob = loader.load(entry.id(), entry, new NoopResolver());

        assertEquals(1, blob.version());
        assertArrayEquals(payload, blob.payload());
        assertEquals(expectedHash(fullFile), blob.contentHash64());
    }

    @Test
    void rejectsInvalidMagic() throws Exception {
        Path baseDir = Files.createTempDirectory("dmesh-loader");
        Path file = baseDir.resolve("bad.dmesh");
        Files.write(file, "not-a-dmesh".getBytes());

        DmeshBlobLoader loader = new DmeshBlobLoader(baseDir);
        ManifestEntry entry = new ManifestEntry(AssetId.of("mesh/bad"), "mesh.packed.dmesh.v0", "bad.dmesh", List.of());

        assertThrows(DynamisException.class, () -> loader.load(entry.id(), entry, new NoopResolver()));
    }

    private static byte[] createDmeshFile(int version, int flags, byte[] payload) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(baos)) {
            out.write(new byte[]{'D', 'M', 'E', 'S', 'H', 0, 0, 0});
            out.writeInt(version);
            out.writeInt(flags);
            out.writeInt(payload.length);
            out.write(payload);
        }
        return baos.toByteArray();
    }

    private static long expectedHash(byte[] bytes) throws Exception {
        byte[] digest = MessageDigest.getInstance("SHA-256").digest(bytes);
        long value = 0L;
        for (int i = 0; i < Long.BYTES; i++) {
            value = (value << 8) | (digest[i] & 0xffL);
        }
        return value;
    }

    private record NoopResolver() implements AssetResolver {
        @Override
        public <T> T resolve(AssetId id, org.dynamiscontent.api.id.AssetType<T> type) {
            throw new UnsupportedOperationException("No dependency resolution in this test");
        }
    }
}
