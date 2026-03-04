package org.dynamiscontent.core.manifest;

import org.dynamis.core.exception.DynamisException;
import org.dynamiscontent.api.id.AssetId;
import org.dynamiscontent.api.manifest.AssetManifest;
import org.dynamiscontent.api.manifest.ManifestEntry;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonAssetManifestReaderTest {

    @Test
    void readsValidManifest() throws Exception {
        Path file = Files.createTempFile("manifest", ".json");
        Files.writeString(file, """
                {
                  "version": 1,
                  "entries": [
                    {
                      "id": "mesh/props/crate",
                      "typeId": "mesh.packed.dmesh.v0",
                      "uri": "cooked/mesh/props/crate.dmesh",
                      "dependencies": ["material/props/crate"]
                    },
                    {
                      "id": "material/props/crate",
                      "typeId": "material.pbr.v0",
                      "uri": "cooked/material/props/crate.mat",
                      "dependencies": []
                    }
                  ]
                }
                """);

        AssetManifest manifest = new JsonAssetManifestReader().read(file);

        ManifestEntry meshEntry = manifest.find(AssetId.of("mesh/props/crate")).orElseThrow();
        assertEquals("mesh.packed.dmesh.v0", meshEntry.typeId());
        assertEquals("cooked/mesh/props/crate.dmesh", meshEntry.uri());
        assertEquals(List.of(AssetId.of("material/props/crate")), meshEntry.dependencies());

        assertTrue(manifest.find(AssetId.of("material/props/crate")).isPresent());
    }

    @Test
    void rejectsInvalidVersion() throws Exception {
        Path file = Files.createTempFile("manifest", ".json");
        Files.writeString(file, """
                { "version": 0, "entries": [] }
                """);

        assertThrows(DynamisException.class, () -> new JsonAssetManifestReader().read(file));
    }

    @Test
    void rejectsMissingRequiredFields() throws Exception {
        Path file = Files.createTempFile("manifest", ".json");
        Files.writeString(file, """
                {
                  "version": 1,
                  "entries": [
                    {
                      "id": "mesh/props/crate",
                      "typeId": "mesh.packed.dmesh.v0",
                      "dependencies": []
                    }
                  ]
                }
                """);

        assertThrows(DynamisException.class, () -> new JsonAssetManifestReader().read(file));
    }
}
