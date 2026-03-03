package org.dynamiscontent.api.manifest;

import org.dynamiscontent.api.id.AssetId;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class AssetManifest {

    private final Map<AssetId, ManifestEntry> entries;

    public AssetManifest(Map<AssetId, ManifestEntry> entries) {
        Objects.requireNonNull(entries, "entries");
        this.entries = Map.copyOf(entries);
    }

    public Optional<ManifestEntry> find(AssetId id) {
        return Optional.ofNullable(entries.get(id));
    }

    public Map<AssetId, ManifestEntry> entries() {
        return entries;
    }
}
