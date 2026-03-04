package org.dynamiscontent.runtime;

import org.dynamiscontent.api.id.AssetId;
import org.dynamiscontent.api.id.AssetTypes;
import org.dynamiscontent.api.manifest.DmeshBlob;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class ContentRuntimeDmeshSmokeTest {

    @Test
    void runtimeLoadsDmeshBlobFromManifestJson() throws Exception {
        Path baseDir = Files.createTempDirectory("content-runtime-dmesh");
        Path dmeshPath = baseDir.resolve("cooked/mesh/props/crate.dmesh");
        Files.createDirectories(dmeshPath.getParent());

        byte[] payload = new byte[]{1, 2, 3, 4};
        byte[] fileBytes = createDmeshFile(1, 0, payload);
        Files.write(dmeshPath, fileBytes);

        Path manifestPath = baseDir.resolve("manifest.json");
        Files.writeString(manifestPath, """
                {
                  "version": 1,
                  "entries": [
                    {
                      "id": "mesh/props/crate",
                      "typeId": "mesh.packed.dmesh.v0",
                      "uri": "cooked/mesh/props/crate.dmesh",
                      "dependencies": []
                    }
                  ]
                }
                """);

        ContentRuntime runtime = ContentRuntime.builder()
                .manifest(manifestPath)
                .baseDir(baseDir)
                .registerDefaultLoaders()
                .build();

        DmeshBlob blob = runtime.assets().get(AssetId.of("mesh/props/crate"), AssetTypes.DMESH_BLOB);

        assertEquals(1, blob.version());
        assertArrayEquals(payload, blob.payload());
        assertEquals(expectedHash(fileBytes), blob.contentHash64());
        assertTrue(blob.payload().length > 0);
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
}
