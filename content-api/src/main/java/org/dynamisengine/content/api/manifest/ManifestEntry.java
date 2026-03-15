package org.dynamisengine.content.api.manifest;

import org.dynamisengine.content.api.id.AssetId;

import java.util.List;
import java.util.Objects;

public record ManifestEntry(
        AssetId id,
        String typeId,
        String uri,
        List<AssetId> dependencies
) {
    public ManifestEntry {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(typeId, "typeId");
        if (typeId.isBlank()) {
            throw new IllegalArgumentException("typeId must not be blank");
        }
        Objects.requireNonNull(uri, "uri");
        if (uri.isBlank()) {
            throw new IllegalArgumentException("uri must not be blank");
        }
        dependencies = dependencies == null ? List.of() : List.copyOf(dependencies);
    }
}
