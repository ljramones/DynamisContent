package org.dynamiscontent.core.manifest;

import org.dynamiscontent.api.id.AssetId;
import org.dynamiscontent.api.manifest.AssetManifest;
import org.dynamiscontent.api.manifest.ManifestEntry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class AssetManifestBuilder {

    private final Map<AssetId, ManifestEntry> entries = new HashMap<>();

    public AssetManifestBuilder add(ManifestEntry entry) {
        Objects.requireNonNull(entry, "entry");
        entries.put(entry.id(), entry);
        return this;
    }

    public AssetManifestBuilder add(AssetId id, String typeId, String uri) {
        return add(new ManifestEntry(id, typeId, uri, List.of()));
    }

    public AssetManifestBuilder add(AssetId id, String typeId, String uri, List<AssetId> deps) {
        return add(new ManifestEntry(id, typeId, uri, deps));
    }

    public AssetManifest build() {
        return new AssetManifest(entries);
    }
}
